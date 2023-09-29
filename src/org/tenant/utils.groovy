package org.tenant

class utils implements Serializable {
    def scmCheckoutToBranch(Map params) {
        def scmUrl = params.scmUrl ?: error("scmUrl is required.")
        def branchName = params.branchName ?: error("scmUrl is required.")
        def credentialsId = params.credentialsId ?: error("credentialsId is required.")
        println(scmUrl + branchName + credentialsId + "hi")
        checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'jenkins-ssh', url: 'https://github.com/pankajakhade/awsproject.git']])
    }
}