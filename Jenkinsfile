pipeline {
    agent {
        label "default-java"
    }

    environment {
        realProjectName = findRealProjectName()
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Going to check out the things !"
                git url: "https://github.com/Terasology/Sample.git", credentialsId: "GooeyHub"
            }
        }
        stage('Prep workspace') {
            steps {
                copyArtifacts(projectName: "Terasology/TerasologySonar", filter: "modules/Core/build.gradle", flatten: true, selector: lastSuccessful())
                copyArtifacts(projectName: "Terasology/TerasologySonar", filter: "*, gradle/wrapper/**, config/**, natives/**", selector: lastSuccessful())
                echo "Real project name: ${env.realProjectName}"
                sh """
                    ls
                    rm -f settings.gradle
                    rm -f gradle.properties
                    echo "rootProject.name = '${env.realProjectName}'" >> settings.gradle
                    cat settings.gradle
                """
            }
        }
        stage('Build') {
            steps {
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
    }
}

def String findRealProjectName() {
    def jobNameParts = env.JOB_NAME.tokenize('/') as String[]
    println "Job name parts: $jobNameParts"
    return jobNameParts.length < 2 ? env.JOB_NAME : jobNameParts[jobNameParts.length - 2]
}
