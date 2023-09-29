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
    node(POD_LABEL) {
        try {
            stage('SCM checkout') {
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'jenkins-ssh', url: 'https://github.com/pankajakhade/awsproject.git']])
            }
            stage('Build and Push Docker Image') {
                docker.withRegistry("https://043196765225.dkr.ecr.us-east-1.amazonaws.com", "ecr:us-east-1:jenkins-ecr-creds") {
                    def customImage = docker.build("jenkins-test:test", "-f docker/dockerFile/Dockerfile .")
                    //customImage.tag("test")
                    customImage.push()
                }
            }
            stage("Slack notification") {
                slackMessage = """Docker image successfully built with below details:
                Image URI: 043196765225.dkr.ecr.us-east-1.amazonaws.com/jenkins-test:test
                Jenkins BUILD_URL: """ + env.BUILD_URL + """
                Jenkins BUILD_LOG: """ + env.BUILD_URL + """/console
                """
                slackSend channel: 'docker-image-builds-notifications', color: 'good', failOnError: true , message: slackMessage, teamDomain: 'storelocal', tokenCredentialId: 'slack-token'
            }
        } catch (Exception e) {
            println(e)
            stage("Slack Send Exception") {
                slackSend channel: 'docker-image-builds-notifications', color: 'danger', failOnError: true , message: e, teamDomain: 'storelocal', tokenCredentialId: 'slack-token'
            }
        }
    }
}