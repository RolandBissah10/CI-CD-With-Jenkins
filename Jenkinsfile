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
                description: 'Send email notifications'
        )
    }

    environment {
        EMAIL_TO = "youremail@example.com" // make sure this is valid
        MAVEN_HOME = tool name: 'Maven 3.9.12', type: 'maven' // must exist in Global Tool Config
    }

    stages {
        stage('Checkout') {
            agent { label 'any' }
            steps {
                checkout([$class: 'GitSCM',
                          branches: [[name: '*/main']],
                          userRemoteConfigs: [[url: 'https://github.com/RolandBissah10/CI-CD-With-Jenkins.git']]])
                script {
                    echo "Branch: main | Author: ${sh(script: 'git log -1 --pretty=%an', returnStdout: true).trim()}"
                }
            }
        }

        stage('Test') {
            agent { label 'any' }
            steps {
                script {
                    // Run Maven tests using configured Maven
                    sh "${MAVEN_HOME}/bin/mvn clean test -B -DBASE_URL=${params.BASE_URL}"

                    // Parse JUnit results
                    def testResults = junit '**/target/surefire-reports/*.xml'
                    env.TEST_TOTAL   = "${testResults.totalCount}"
                    env.TEST_PASSED  = "${testResults.passCount}"
                    env.TEST_FAILED  = "${testResults.failCount}"
                    env.TEST_SKIPPED = "${testResults.skipCount}"

                    // Gather failed test names
                    def fileList = sh(returnStdout: true, script: 'ls target/surefire-reports/TEST-*.xml 2>/dev/null || true').trim()
                    def failedItems = []
                    if (fileList) {
                        fileList.split('\n').each { file ->
                            def content = readFile(file)
                            def matcher = content =~ /<testcase .*?classname="(.*?)".*?name="(.*?)".*?failure/
                            matcher.each { m ->
                                failedItems << "${m[1]}.${m[2]}"
                            }
                        }
                    }
                    env.TEST_FAILURE_LIST = failedItems ? failedItems.join("\n") : " - None (All tests passed)"

                    echo "Total Tests: ${env.TEST_TOTAL}"
                    echo "Passed: ${env.TEST_PASSED}"
                    echo "Failed: ${env.TEST_FAILED}"
                    echo "Skipped: ${env.TEST_SKIPPED}"
                    echo "Failed Tests:\n${env.TEST_FAILURE_LIST}"
                }
            }
        }

        stage('Allure Report') {
            agent { label 'any' }
            steps {
                script {
                    allure([
                            reportBuildPolicy: 'ALWAYS',
                            results: [[path: 'target/allure-results']]
                    ])
                }
            }
        }
    }

    post {
        always {
            node { // wrap in node to allow mail/send operations
                script {
                    if (params.SEND_NOTIFICATIONS && env.EMAIL_TO) {
                        def status = currentBuild.currentResult
                        mail to: "${env.EMAIL_TO}",
                                subject: "Jenkins Build ${status}: ${currentBuild.fullDisplayName}",
                                body: """Build ${status} for ${env.JOB_NAME} #${env.BUILD_NUMBER}
Branch: main
Total Tests: ${env.TEST_TOTAL ?: 'N/A'}
Passed: ${env.TEST_PASSED ?: 'N/A'}
Failed: ${env.TEST_FAILED ?: 'N/A'}
Skipped: ${env.TEST_SKIPPED ?: 'N/A'}
Failed Tests List: ${env.TEST_FAILURE_LIST ?: 'N/A'}
Check Jenkins for details: ${env.BUILD_URL}"""
                    } else {
                        echo "Notification skipped: SEND_NOTIFICATIONS=false or EMAIL_TO not defined"
                    }
                }
            }
        }
    }
}