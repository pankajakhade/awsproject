package org.tenant

class utils implements Serializable {
    def scmCheckoutToBranch(scmUrl, credentialsId, branchName) {
        checkout scmGit(branches: [[name: "*/$branchName"]], extensions: [], userRemoteConfigs: [[credentialsId: credentialsId, url: scmUrl]])
    }
}