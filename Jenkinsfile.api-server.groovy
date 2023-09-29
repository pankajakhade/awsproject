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
        def imageTag = null
        def ecrRepoURL = "https://043196765225.dkr.ecr.us-east-1.amazonaws.com"
        def repoName = "jenkins-test"
        def region = "us-east-1"
        def ecrCredsInJenkins = "jenkins-ecr-creds"
        def dockerFilePath = "docker/dockerFile/Dockerfile"
        def dockerBuildContent = "."

        def scmUrl = "https://github.com/pankajakhade/awsproject.git"
        def branchName = "master"
        def gitCredentialsId = "jenkins-ssh"
        try {
            stage('SCM checkout') {
                scmCheckout.scmCheckoutAtBranch(scmUrl, branchName, gitCredentialsId)
            }
            stage('Build and Push Docker Image') {
                imageTag = env.BRANCH_NAME + "." + sh(returnStdout: true, script: 'git rev-parse --short HEAD').toString().trim()
                def customImage = dockerOperations.dockerBuild(repoName, imageTag, dockerFilePath, dockerBuildContent)

                dockerOperations.dockerPush(ecrRepoURL, region, ecrCredsInJenkins, customImage)
            }
            stage("Slack notification") {
                slack.slackSendSuccessMessage(ecrRepoURL, repoName, imageTag, 'docker-image-builds-notifications')
            }
        } catch (Exception e) {
            stage("Slack Send Error") {
                println(e)
                slack.slackSendFailureMessage('docker-image-builds-notifications')
            }
        }
    }
}