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
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'jenkins', url: 'https://github.com/pankajakhade/awsproject.git']])
            }
            stage('Build and Push Docker Image') {
                docker.withRegistry("https://043196765225.dkr.ecr.us-east-1.amazonaws.com", "ecr:us-east-1:arn:aws:iam::043196765225:role/mgmt-eks-node-group-role") {
                    def customImage = docker.build("jenkins-test:test", "-f docker/dockerFile/Dockerfile .")
                    //customImage.tag("test")
                    customImage.push()
                }
            }
        } catch (Exception e) {
            println(e)
        }
    }
}