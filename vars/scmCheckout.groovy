def scmCheckoutAtBranch(scmUrl, branchName, gitCredentialsId) {
    checkout scmGit(branches: [[name: '*/' + branchName]], extensions: [], userRemoteConfigs: [[credentialsId: gitCredentialsId, url: scmUrl]])
}