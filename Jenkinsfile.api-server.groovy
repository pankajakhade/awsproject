podTemplate(yaml: '''
    apiVersion: v1
    kind: Pod
    spec:
      containers:
      - name: docker
        image: docker:20.10-dind  # Docker-in-Docker image
        securityContext:
          privileged: true  # Required for running Docker inside Docker
        volumeMounts:
          - name: docker-data
            mountPath: /var/lib/docker
      - image: "jenkins/inbound-agent:3142.vcfca_0cd92128-1"
        name: "jnlp"
        resources:
          requests:
            memory: "256Mi"
            cpu: "100m"
        volumeMounts:
        - mountPath: "/home/jenkins/agent"
          name: "workspace-volume"
          readOnly: false
      - env:
        - name: "JENKINS_URL"
          value: "http://jenkins.jenkins.svc.cluster.local:8080/"
        - name: "DOCKER_HOST"
          value: "tcp://localhost:2375"
      volumes:
      - name: docker-data
        hostPath:
          path: /var/lib/docker
          type: Directory
      - name: "workspace-volume"
        emptyDir:
          medium: ""
      restartPolicy: "Never"
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
            sh "docker images"
        }
    }
}