package org.tenant

class jenkinsCI {
    def scmCheckoutToBranch(scmUrl, credentialsId, branchName) {
        checkout scmGit(branches: [[name: "*/$branchName"]], extensions: [], userRemoteConfigs: [[credentialsId: credentialsId, url: scmUrl]])
    }
}