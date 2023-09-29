podTemplate(yaml: '''
    apiVersion: v1
    kind: Pod
    spec:
      containers:
      - name: docker
        image: docker:dind  # Docker-in-Docker image
        command: ["dockerd", "--tls=false", "--host=unix:///var/run/docker.sock", "--host=tcp://localhost:2375"]
        securityContext:
          privileged: true  # Required for running Docker inside Docker
        volumeMounts:
          - name: docker-data
            mountPath: /var/lib/docker
      - image: "043196765225.dkr.ecr.us-east-1.amazonaws.com/jenkins-test:latest"
        name: "jnlp"
        resources:
          requests:
            memory: "256Mi"
            cpu: "100m"
        env:
        - name: "DOCKER_HOST"
          value: "tcp://localhost:2375"
        volumeMounts:
        - mountPath: "/home/jenkins/agent"
          name: "workspace-volume"
          readOnly: false
      volumes:
      - name: docker-data
        emptyDir: {}
      - name: "workspace-volume"
        emptyDir:
          medium: ""
      restartPolicy: "Never"
      imagePullPolicy: Always
''')  {
    @Library('jenkins-shared-library') _
    //import org.tenant.jenkinsCI //For future use
    //def object = new jenkinsCI() //For future use
    node(POD_LABEL) {
        try {
            stage('SCM checkout') {
                scmCheckout.scmCheckoutAtBranch(scmUrl: "https://github.com/pankajakhade/awsproject.git", branchName: "master", credentialsId: "jenkins-ssh")
            }
            stage('Build and Push Docker Image') {
                def imageTag = env.BRANCH_NAME + "." + sh(returnStdout: true, script: 'git rev-parse --short HEAD').toString().trim()
                def ecrRepoURL = "https://043196765225.dkr.ecr.us-east-1.amazonaws.com"
                def region = "us-east-1"
                def ecrCredsInJenkins = "jenkins-ecr-creds"
                def customImage = dockerOperations.dockerBuild(repoName: "jenkins-test", imageTag: imageTag,
                dockerFilePath: "docker/dockerFile/Dockerfile",
                dockerBuildContent: ".")

                dockerOperations.dockerPush(ecrRepoURL: ecrRepoURL,
                 region: region,
                 ecrCredsInJenkins: ecrCredsInJenkins,
                 customImage: customImage)
            }
            stage("Slack notification") {
                slackMessage = env.JOB_NAME + """ with Branch=""" + env.BRANCH_NAME + """ finished successfully:
        Image URI: 043196765225.dkr.ecr.us-east-1.amazonaws.com/jenkins-test:test
        Jenkins BUILD_URL: """ + env.BUILD_URL + """
        Jenkins BUILD_LOG: """ + env.BUILD_URL + """console
        """
                slackSend channel: 'docker-image-builds-notifications', color: 'good', failOnError: true , message: slackMessage, teamDomain: 'storelocal', tokenCredentialId: 'slack-token'
            }
        } catch (Exception e) {
            stage("Slack Send Error") {
                slackMessage = env.JOB_NAME + """ Job with Branch=""" + env.BRANCH_NAME + """ is failed:
        Jenkins BUILD_URL: """ + env.BUILD_URL + """
        Jenkins BUILD_LOG: """ + env.BUILD_URL + """console
        Exception/Error: """ + e
                println(e)
                slackSend channel: 'docker-image-builds-notifications', color: 'danger', failOnError: true , message: slackMessage, teamDomain: 'storelocal', tokenCredentialId: 'slack-token'
            }
        }
    }
}