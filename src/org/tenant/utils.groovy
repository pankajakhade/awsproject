package org.tenant

def scmCheckoutToBranch(scmUrl, credentialsId, branchName) {
    checkout scmGit(branches: [[name: "*/$branchName"]], extensions: [], userRemoteConfigs: [[credentialsId: credentialsId, url: scmUrl]])
}

return this