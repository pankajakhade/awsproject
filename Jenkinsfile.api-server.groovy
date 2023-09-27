podTemplate(yaml: '''
    apiVersion: v1
    kind: Pod
    spec:
      containers:
      - name: jnlp
        image: jenkins/inbound-agent:3107.v665000b_51092-15
        resources:
          limits:
            cpu: "500m"
            memory: "512Mi"
          requests:
            cpu: "100m"
            memory: "256Mi"
      - name: docker
        image: docker:dind  # Docker-in-Docker image
        securityContext:
          privileged: true  # Required for running Docker inside Docker
        volumeMounts:
          - name: docker-socket
            mountPath: /var/run/docker.sock
      volumes:
        - name: docker-socket
          hostPath:
            path: /var/run/docker.sock
''')  {
    node(POD_LABEL) {
        stage('SCM checkout') {
            checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'jenkins', url: 'https://github.com/pankajakhade/awsproject.git']])
        }
        stage('Build and Push Docker Image') {
//             docker.withRegistry('043196765225.dkr.ecr.us-east-1.amazonaws.com') {
//                 def customImage = docker.build(jenkins-test, "-f docker/dockerFile/Dockerfile .")
//                 customImage.tag("jenkins-test:test")
//                 customImage.push()
//             }
            sh "docker"
        }
    }
}