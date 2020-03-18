node ("default-java") {

    stage('Checkout') {
        echo "Going to check out the things !"
        checkout scm
    }

    stage('Prep workspace') {
        copyArtifacts(projectName: "Nanoware/Terasology/develop", filter: "modules/Core/build.gradle", flatten: true, selector: lastSuccessful())
        copyArtifacts(projectName: "Nanoware/Terasology/develop", filter: "*, gradle/wrapper/**, config/**, natives/**", selector: lastSuccessful())
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
    
    stage('Analytics') {
        sh './gradlew check'
    }
    
    stage('Publish') {
        withCredentials([usernamePassword(credentialsId: 'artifactory-gooey', usernameVariable: 'artifactoryUser', passwordVariable: 'artifactoryPass')]) {
            sh './gradlew -Dorg.gradle.internal.publish.checksums.insecure=true publish -PmavenUser=${artifactoryUser} -PmavenPass=${artifactoryPass}'
        }
    }
    
    stage('Record') {
        junit testResults: 'build/test-results/test/*.xml',  allowEmptyResults: true
        recordIssues tool: javaDoc()
        step([$class: 'JavadocArchiver', javadocDir: 'build/docs/javadoc', keepAll: false])
        recordIssues tool: checkStyle(pattern: '**/build/reports/checkstyle/*.xml')
        recordIssues tool: spotBugs(pattern: '**/build/reports/spotbugs/*.xml', useRankAsPriority: true)
        recordIssues tool: pmdParser(pattern: '**/build/reports/pmd/*.xml')
        recordIssues tool: taskScanner(includePattern: '**/*.java,**/*.groovy,**/*.gradle', lowTags: 'WIBNIF', normalTags: 'TODO', highTags: 'ASAP')
    }
}

String findRealProjectName() {
    def jobNameParts = env.JOB_NAME.tokenize('/') as String[]
    println "Job name parts: $jobNameParts"
    return jobNameParts.length < 2 ? env.JOB_NAME : jobNameParts[jobNameParts.length - 2]
}
