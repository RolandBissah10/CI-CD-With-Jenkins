pipeline {
    agent any

    options {
        timestamps()
        ansiColor('xterm')
        timeout(time: 20, unit: 'MINUTES')
    }

    parameters {
        string(name: 'BASE_URL', defaultValue: 'https://fakestoreapi.com', description: 'Target API base URL')
        booleanParam(name: 'SEND_NOTIFICATIONS', defaultValue: true, description: 'Send email notifications')
    }

    environment {
        EMAIL_TO = 'you@example.com'  // replace with actual recipient
        MAVEN_HOME = tool(name: 'Maven 3.9.12', type: 'maven')
    }

    stages {

        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM',
                          branches: [[name: '*/main']],
                          userRemoteConfigs: [[url: 'https://github.com/RolandBissah10/CI-CD-With-Jenkins.git']]])
                script {
                    def author = sh(script: 'git log -1 --pretty=%an', returnStdout: true).trim()
                    echo "Branch: main | Author: ${author}"
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    sh "${env.MAVEN_HOME}/bin/mvn clean test -B -DBASE_URL=${params.BASE_URL}"
                }
            }
            post {
                always {
                    script {
                        // Collect JUnit results
                        def testResults = junit '**/target/surefire-reports/*.xml'
                        env.TEST_TOTAL   = "${testResults.totalCount}"
                        env.TEST_PASSED  = "${testResults.passCount}"
                        env.TEST_FAILED  = "${testResults.failCount}"
                        env.TEST_SKIPPED = "${testResults.skipCount}"

                        // Prepare failed test list
                        def failedItems = []
                        def files = sh(script: 'ls target/surefire-reports/TEST-*.xml 2>/dev/null || true', returnStdout: true).trim()
                        if (files) {
                            files.split("\n").each { file ->
                                def failed = readFile(file) =~ /<testcase.*?failure.*?<\/testcase>/
                                failed.each { f -> failedItems.add(f[0]) }
                            }
                        }
                        env.TEST_FAILURE_LIST = failedItems ? failedItems.join("\n") : " - None (All tests passed)"

                        echo "Total: ${env.TEST_TOTAL}, Passed: ${env.TEST_PASSED}, Failed: ${env.TEST_FAILED}, Skipped: ${env.TEST_SKIPPED}"
                        echo "Failed tests:\n${env.TEST_FAILURE_LIST}"
                    }
                }
            }
        }

        stage('Allure Report') {
            steps {
                script {
                    // Only attempt if Allure CLI is installed
                    if (fileExists("/usr/local/bin/allure") || toolExists("Allure")) {
                        allure([
                                includeProperties: false,
                                jdk: '',
                                results: [[path: 'target/allure-results']]
                        ])
                    } else {
                        echo "Allure CLI not found, skipping report generation"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline succeeded!"
        }
        failure {
            echo "Pipeline failed!"
            script {
                if (params.SEND_NOTIFICATIONS) {
                    mail to: "${env.EMAIL_TO}",
                            subject: "Jenkins Build FAILED: ${currentBuild.fullDisplayName}",
                            body: """Build failed for ${env.JOB_NAME} #${env.BUILD_NUMBER}
Branch: main
Failed Tests: ${env.TEST_FAILURE_LIST}
Check Jenkins for details: ${env.BUILD_URL}"""
                }
            }
        }
    }
}