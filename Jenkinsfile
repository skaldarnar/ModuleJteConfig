node ("default-java") {

    stage('Checkout') {
        echo "Going to check out the things !"
        checkout scm
    }

    stage('Prep workspace') {
        copyArtifacts(projectName: "Nanoware/Terasology/PR-89", filter: "modules/Core/build.gradle", flatten: true, selector: lastSuccessful())
        copyArtifacts(projectName: "Nanoware/Terasology/PR-89", filter: "*, gradle/wrapper/**, config/**, natives/**", selector: lastSuccessful())
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
        sh './gradlew clean jar'
        archiveArtifacts 'gradlew, gradle/wrapper/*, modules/Core/build.gradle, config/**, build/distributions/Terasology.zip, build/resources/main/org/terasology/version/versionInfo.properties, natives/**'
    }
    
    stage('Publish') {
        withCredentials([usernamePassword(credentialsId: 'artifactory-gooey', usernameVariable: 'artifactoryUser', passwordVariable: 'artifactoryPass')]) {
            sh './gradlew publish -PmavenUser=${artifactoryUser} -PmavenPass=${artifactoryPass}'
        }
    }
}

def String findRealProjectName() {
    def jobNameParts = env.JOB_NAME.tokenize('/') as String[]
    println "Job name parts: $jobNameParts"
    return jobNameParts.length < 2 ? env.JOB_NAME : jobNameParts[jobNameParts.length - 2]
}
