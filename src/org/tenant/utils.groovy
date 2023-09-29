package org.tenant

class utils implements Serializable {
    def scmCheckoutToBranch(Map params) {
        checkout scmGit(branches: [[name: "*/" + params['branchName']]], extensions: [], userRemoteConfigs: [[credentialsId: params['credentialsId'], url: params['scmUrl']]])
    }
}