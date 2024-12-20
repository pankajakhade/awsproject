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
        def (ecrRepoURL, repoName, region, ecrCredsInJenkins, dockerFilePath, dockerBuildContent) = [
                "https://043196765225.dkr.ecr.us-east-1.amazonaws.com",
                "jenkins-test",
                "us-east-1",
                "jenkins-ecr-creds",
                "docker/dockerFile/Dockerfile",
                "."
            ]
        def (scmUrl, branchName, gitCredentialsId) = [
                "https://github.com/pankajakhade/awsproject.git",
                "master",
                "jenkins-ssh"
            ]

        try {
            stage('SCM checkout') {
                scmCheckout.scmCheckoutAtBranch(scmUrl, branchName, gitCredentialsId)
            }
            stage('Build and Push Docker Image') {
                imageTag = dockerOperations.dockerBuildAndPush(repoName, dockerFilePath, dockerBuildContent,
                                ecrRepoURL, region, ecrCredsInJenkins)
            }
            stage("Slack notification") {
                slackSendMessages.slackSendSuccessMessage(ecrRepoURL, repoName, imageTag, 'docker-image-builds-notifications')
            }
        } catch (Exception e) {
            stage("Slack Send Error") {
                println(e)
                slackSendMessages.slackSendFailureMessage('docker-image-builds-notifications', e)
            }
        }
    }
}