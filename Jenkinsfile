pipeline {
    agent none

    parameters {
        string(name: 'BASE_URL', defaultValue: 'https://fakestoreapi.com', description: 'Target API base URL')
        booleanParam(name: 'SEND_NOTIFICATIONS', defaultValue: true, description: 'Send Slack + email notifications')
    }

    environment {
        BASE_URL = "${params.BASE_URL ?: 'https://fakestoreapi.com'}"
        EMAIL_TO = credentials('QA_EMAIL_RECIPIENT')
    }

    stages {

        stage('Checkout') {
            agent any
            steps {
                git branch: 'main', url: 'https://github.com/RolandBissah10/CI-CD-With-Jenkins.git'
            }
        }

        stage('Test') {
            agent { docker { image 'maven:3.9.2-openjdk-17' } }
            steps {
                sh "mvn clean test -B -DBASE_URL=${env.BASE_URL}"
            }
            post {
                always {
                    script {
                        if (fileExists('target/allure-results') || fileExists('target/surefire-reports')) {
                            stash name: 'results', includes: 'target/allure-results/**, target/surefire-reports/**'
                        }
                    }
                }
            }
        }

        stage('Reports') {
            agent any
            steps {
                unstash 'results'

                allure results: [[path: 'target/allure-results']]

                publishHTML([
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/surefire-reports',
                        reportFiles: 'index.html',
                        reportName: 'API Test Reports'
                ])

                archiveArtifacts(
                        artifacts: 'target/surefire-reports/**/*.xml, target/allure-report/**',
                        allowEmptyArchive: true
                )

                script {
                    def testResults = junit '**/target/surefire-reports/*.xml'
                    env.TEST_TOTAL = "${testResults.totalCount}"
                    env.TEST_PASSED = "${testResults.passCount}"
                    env.TEST_FAILED = "${testResults.failCount}"
                    env.TEST_SKIPPED = "${testResults.skipCount}"
                }
            }
        }
    }

    post {
        always {
            node {
                cleanWs()
            }
        }
    }
}