pipeline {
    agent none

    parameters {
        string(
                name: 'BASE_URL',
                defaultValue: 'https://fakestoreapi.com',
                description: 'Target API base URL'
        )
        booleanParam(
                name: 'SEND_NOTIFICATIONS',
                defaultValue: true,
                description: 'Send Slack + email notifications'
        )
    }

    environment {
        BASE_URL = "${params.BASE_URL ?: 'https://fakestoreapi.com'}"
        EMAIL_TO = credentials('QA_EMAIL_RECIPIENT')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        disableConcurrentBuilds()
        timeout(time: 20, unit: 'MINUTES')
        timestamps()
        // ❌ removed ansiColor (plugin missing)
    }

    triggers {
        githubPush()
        cron('H 2 * * *')
    }

    stages {

        stage('Checkout') {
            agent any
            steps {
                git branch: 'main', url: 'https://github.com/RolandBissah10/CI-CD-With-Jenkins.git'
                script {
                    env.GIT_AUTHOR = isUnix() ?
                            sh(script: 'git log -1 --pretty=%an', returnStdout: true).trim() :
                            bat(script: 'git log -1 --pretty=%an', returnStdout: true).trim()
                }
                echo "Branch: ${env.BRANCH_NAME ?: 'main'} | Author: ${env.GIT_AUTHOR}"
            }
        }

        stage('Test') {
            agent any   // ✅ replaced docker agent
            steps {
                script {
                    if (isUnix()) {
                        sh "mvn clean test -B -DBASE_URL=${env.BASE_URL}"
                    } else {
                        bat "mvn clean test -B -DBASE_URL=${env.BASE_URL}"
                    }
                }
            }
            post {
                always {
                    script {
                        if (isUnix()) {
                            sh '''
                            if [ -d allure-results ]; then
                                mkdir -p target/allure-results
                                cp -r allure-results/* target/allure-results/
                            fi
                            chmod -R 777 ${WORKSPACE}
                            '''
                        }
                    }
                    stash name: 'results', includes: 'target/allure-results/**, target/surefire-reports/**'
                }
            }
        }

        stage('Reports') {
            agent any
            steps {
                script {
                    if (isUnix()) {
                        sh 'rm -rf ${WORKSPACE}/* ${WORKSPACE}/.[!.]* 2>/dev/null || true'
                    }
                }

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
        cleanup {
            cleanWs()
        }
    }
}