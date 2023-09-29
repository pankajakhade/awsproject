package org.tenant

class utils {
    def scmCheckoutToBranch(scmUrl, credentialsId, branchName) {
        checkout scmGit(branches: [[name: "*/$branchName"]], extensions: [], userRemoteConfigs: [[credentialsId: credentialsId, url: scmUrl]])
    }
}