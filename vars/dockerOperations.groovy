
def dockerBuildAndPush(repoName, dockerFilePath, dockerBuildContent, ecrRepoURL, region, ecrCredsInJenkins) {
    imageTag = env.BRANCH_NAME + "." + sh(returnStdout: true, script: 'git rev-parse --short HEAD').toString().trim()
    docker.withRegistry(ecrRepoURL, "ecr:" + region + ":" + ecrCredsInJenkins) {
        def customImage = docker.build(repoName + ":" + imageTag, "-f " + dockerFilePath + " " + dockerBuildContent)
        customImage.push()
    
}