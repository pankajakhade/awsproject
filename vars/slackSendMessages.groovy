def slackSendSuccessMessage(ecrRepoURL, repoName, imageTag, channelName) {
    slackMessage = """**JobName=""" + env.JOB_NAME + """ with BranchName=""" + env.BRANCH_NAME + """ finished successfully:**
Image URI: """ + ecrRepoURL + """/""" + repoName + """:""" + imageTag + """
Jenkins BUILD_URL: """ + env.BUILD_URL + """
Jenkins BUILD_LOG: """ + env.BUILD_URL + """console
"""
    slackSend channel: channelName, color: 'good', failOnError: true , message: slackMessage, teamDomain: 'storelocal', tokenCredentialId: 'slack-token'
}

def slackSendFailureMessage(channelName, exception) {
    slackMessage = """**JobName=""" + env.JOB_NAME + """ with BranchName=""" + env.BRANCH_NAME + """ is failed:**
Jenkins BUILD_URL: """ + env.BUILD_URL + """
Jenkins BUILD_LOG: """ + env.BUILD_URL + """console
Exception/Error: \n============\n""" + exception
    slackSend channel: channelName, color: 'danger', failOnError: true , message: slackMessage, teamDomain: 'storelocal', tokenCredentialId: 'slack-token'
}