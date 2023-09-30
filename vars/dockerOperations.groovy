
def dockerBuild(repoName, imageTag, dockerFilePath, dockerBuildContent) {
    def customImage = docker.build(repoName + ":" + imageTag, "-f " + dockerFilePath + " " + dockerBuildContent)
    return customImage
}

def dockerPush(ecrRepoURL, region, ecrCredsInJenkins, customImage) {
    docker.withRegistry(ecrRepoURL, "ecr:" + region + ":" + ecrCredsInJenkins) {
        customImage.push()
    }
}