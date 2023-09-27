podTemplate {
    node(POD_LABEL) {
        stage('SCM checkout') {
            checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'jenkins', url: 'https://github.com/pankajakhade/awsproject.git']])
        }
        stage('Build and Push Docker Image') {
            //docker.withRegistry('043196765225.dkr.ecr.us-east-1.amazonaws.com') {
            def customImage = docker.build(jenkins-test, "-f docker/dockerFile/Dockerfile .")
            //customImage.tag("jenkins-test:test")
            //customImage.push()
            //}
        }
    }
}