
def dockerBuild() {
    docker.withRegistry("https://043196765225.dkr.ecr.us-east-1.amazonaws.com", "ecr:us-east-1:jenkins-ecr-creds") {
        def customImage = docker.build("jenkins-test:test", "-f docker/dockerFile/Dockerfile .")
        //customImage.tag("test")
        customImage.push()
    }
}

def dockerBuild(Map params) {
    def customImage = docker.build(params.repoName + ":" + params.imageTag, "-f " + params.dockerFilePath + " " + params.dockerBuildContent)
    return customImage
}

def dockerPush(Map params) {
    docker.withRegistry(params.ecrRepoURL, "ecr:" + params.region + ":" + params.ecrCredsInJenkins) {
        params.customImage.push()
    }
}