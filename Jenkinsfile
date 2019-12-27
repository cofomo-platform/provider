pipeline {
    agent any
    
    tools {
        maven 'M3'
        jdk 'openjdk-13'
    }

    environment {
        // Extract information from pom.xml
        IMAGE = readMavenPom().getArtifactId()
        VERSION = readMavenPom().getVersion()
        DOCUMENTATIONURL = "https://provider.cofomo.io"
    }

    stages {
        stage('Build Maven') {
            steps {
                // Get some code from a GitHub repository
                git 'https://github.com/cofomo-platform/provider'

                // Run Maven
                sh "mvn -Dspring.profiles.active=prod clean package"
            }

            post {
                // If Maven was able to run the tests, even if some of the test
                // failed, record the test results and archive the jar file.
                success {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                }
            }
        }
        stage('Copy Documentation') {
            steps {
                // Copy all documentation snippets to parent folder
                sh "cp -r ./target/generated-snippets/. ../DOCS/generated-snippets"
                sh "cp -r ./src/main/asciidoc/index.adoc ../DOCS/sourcefiles/provider.adoc"
            }
        }

        stage('Build Docker Image') {
            steps {
                // This builds the container
                sh "docker build . --build-arg version=${VERSION} -t provider:${VERSION}"
            }
        }
        stage('Build Docker Container') {
            steps {
                // This runs the container
                sh "docker container stop provider"
                sh "docker container rm provider"
                sh "docker run --name provider -p 8083:8083 --network cofomo --restart always -d provider:${VERSION}"
            }
            post {
                 // Cleanup
                success {
                    sh "docker system prune"
                }
            }
        }
    }
    post { 
    	// Send success message to Slack
        success { 
            slackSend color: "good", message: "provider:${VERSION} successfully built and deployed in ${currentBuild.durationString}.\nGo to ${DOCUMENTATIONURL} for more information"
        }
        // Send failure message to Slack
        failure {
            slackSend color: "bad", message: "Failure in build and deployment of provider:${VERSION}"
        }
        
    }
}