node ("default-java") {

    stage('Checkout') {
        echo "Going to check out the things !"
        checkout scm
    }

    stage('Prep workspace') {
        copyArtifacts(projectName: "Terasology/TerasologySonar", filter: "modules/Core/build.gradle", flatten: true, selector: lastSuccessful())
        copyArtifacts(projectName: "Terasology/TerasologySonar", filter: "*, gradle/wrapper/**, config/**, natives/**", selector: lastSuccessful())
        def realProjectName = findRealProjectName()
        echo "Real project name: $realProjectName"
        sh """
            ls
            rm -f settings.gradle
            rm -f gradle.properties
            echo "rootProject.name = '$realProjectName'" >> settings.gradle
            cat settings.gradle
            chmod +x gradlew
        """
    }
    stage('Build') {
        rtGradleResolver (
            id: 'teraResolver',
            serverId: 'TerasologyArtifactory',
            repo: 'virtual-repo-live'
        )
          
        rtGradleDeployer (
            id: 'teraDeployer',
            serverId: 'TerasologyArtifactory',
            repo: 'terasology-snapshot-local',
        )

        rtGradleRun (
            // Set to true if the Artifactory Plugin is already defined in build script.
            usesPlugin: true,
            // Set to true if you'd like the build to use the Gradle Wrapper.
            useWrapper: true,
            tasks: 'clean check jar generatePomFileForMavenJavaPublication artifactoryPublish',
            resolverId: 'teraResolver',
            deployerId: 'teraDeployer',
        )

        rtPublishBuildInfo (
            serverId: 'TerasologyArtifactory'
        )
    }
}

def String findRealProjectName() {
    def jobNameParts = env.JOB_NAME.tokenize('/') as String[]
    println "Job name parts: $jobNameParts"
    return jobNameParts.length < 2 ? env.JOB_NAME : jobNameParts[jobNameParts.length - 2]
}
