def scmCheckoutToBranch(Map params) {
    def scmUrl = params.scmUrl ?: error("scmUrl is required.")
    def branchName = params.branchName ?: error("branchName is required.")
    def credentialsId = params.credentialsId ?: error("credentialsId is required.")
    checkout scmGit(branches: [[name: '*/' + branchName]], extensions: [], userRemoteConfigs: [[credentialsId: credentialsId, url: scmUrl]])
}