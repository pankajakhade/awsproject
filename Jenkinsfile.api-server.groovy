podTemplate {
    node(POD_LABEL) {
        stage('Run shell') {
            sh 'ls -ltr'
        }
    }
}