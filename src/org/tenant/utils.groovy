package org.tenant

class utils implements Serializable {
    def scmCheckoutToBranch(Map params) {
        println(params)
        //checkout scmGit(branches: [[name: "*/$branchName"]], extensions: [], userRemoteConfigs: [[credentialsId: credentialsId, url: scmUrl]])
    }
}