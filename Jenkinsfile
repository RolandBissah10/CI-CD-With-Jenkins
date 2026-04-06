pipeline {
    agent none

    environment {
        EMAIL_TO = 'you@example.com'   // <-- replace with your email
        BASE_URL  = 'https://fakestoreapi.com'
    }

    stages {
        stage('Checkout') {
            agent any
            steps {
                checkout scm
                echo "Checked out branch ${env.BRANCH_NAME}"
            }
        }

        stage('Test') {
            agent any
            steps {
                script {
                    // Run Maven in Docker to ensure consistent environment
                    sh """
                        docker run --rm -v \$(pwd):/app -w /app maven:3.9.12-eclipse-temurin-17 \
                        mvn clean test -B -DBASE_URL=${BASE_URL}
                    """
                }
            }
            post {
                always {
                    script {
                        // Capture JUnit test results
                        def testResults = junit '**/target/surefire-reports/*.xml'
                        env.TEST_TOTAL   = "${testResults.totalCount}"
                        env.TEST_PASSED  = "${testResults.passCount}"
                        env.TEST_FAILED  = "${testResults.failCount}"
                        env.TEST_SKIPPED = "${testResults.skipCount}"

                        echo "Total tests: ${env.TEST_TOTAL}, Passed: ${env.TEST_PASSED}, Failed: ${env.TEST_FAILED}, Skipped: ${env.TEST_SKIPPED}"

                        // Generate Allure report if results exist
                        if (fileExists('target/allure-results')) {
                            allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
                        } else {
                            echo "No Allure results found."
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                def buildStatus = currentBuild.currentResult
                echo "Sending notification: Build ${buildStatus}"

                mail to: "${EMAIL_TO}",
                        subject: "Jenkins Build: ${currentBuild.fullDisplayName} - ${buildStatus}",
                        body: """Build Details:
Job: ${env.JOB_NAME}
Build: ${env.BUILD_NUMBER}
Status: ${buildStatus}
Branch: ${env.BRANCH_NAME}
Total Tests: ${env.TEST_TOTAL ?: 'N/A'}
Passed: ${env.TEST_PASSED ?: 'N/A'}
Failed: ${env.TEST_FAILED ?: 'N/A'}
Skipped: ${env.TEST_SKIPPED ?: 'N/A'}

Check console output at ${env.BUILD_URL}"""
            }
        }
    }
}