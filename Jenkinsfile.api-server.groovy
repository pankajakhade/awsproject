podTemplate {
    node(POD_LABEL) {
        stage('SCM checkout') {
            checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'jenkins', url: 'https://github.com/pankajakhade/awsproject.git']])
        }
    }
}