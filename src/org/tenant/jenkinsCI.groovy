package org.tenant

class jenkinsCI {
    def scmCheckoutToBranch(scmUrl, credentialsId, branchName) {
        println("Hello World")
        //checkout scmGit(branches: [[name: "*/$branchName"]], extensions: [], userRemoteConfigs: [[credentialsId: credentialsId, url: scmUrl]])
    }
}