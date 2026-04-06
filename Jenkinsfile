pipeline {
    agent none

    parameters {
        string(
                name:         'BASE_URL',
                defaultValue: 'https://fakestoreapi.com',
                description:  'Target API base URL'
        )
        booleanParam(
                name:         'SEND_NOTIFICATIONS',
                defaultValue: true,
                description:  'Send Slack + email notifications'
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
        ansiColor('xterm')
    }

    triggers {
        githubPush()
        cron('H 2 * * *')
    }

    stages {

        stage('Checkout') {
            agent any
            steps {
                checkout scm: [$class: 'GitSCM', branches: [[name: 'main']],
                               userRemoteConfigs: [[url: 'https://github.com/RolandBissah10/CI-CD-With-Jenkins.git']]]
                script {
                    env.GIT_AUTHOR = sh(script: 'git log -1 --pretty=%an', returnStdout: true).trim()
                }
                echo "Branch: ${env.BRANCH_NAME ?: 'main'} | Author: ${env.GIT_AUTHOR}"
            }
        }

        stage('Test') {
            agent any
            steps {
                script {
                    def mvnHome = tool name: 'maven3', type: 'maven'
                    withEnv(["PATH+MAVEN=${mvnHome}/bin"]) {
                        sh "mvn clean test -B -DBASE_URL=${env.BASE_URL}"
                    }
                }
            }
            post {
                always {
                    // Merge any root-level allure-results into target/ as a fallback
                    sh 'if [ -d allure-results ]; then mkdir -p target/allure-results && cp -r allure-results/* target/allure-results/; fi'
                    stash name: 'results', includes: 'target/allure-results/**, target/surefire-reports/**'
                    sh 'chmod -R 777 ${WORKSPACE}'
                }
            }
        }

        stage('Reports') {
            agent any
            steps {
                // Only wipe the target/ folder — leave .git and workspace metadata intact
                sh 'rm -rf target 2>/dev/null || true'
                unstash 'results'

                // NOTE: 'allure' must match the name configured in
                // Manage Jenkins → Global Tool Configuration → Allure Commandline
                allure([
                        includeProperties: false,
                        jdk:               '',
                        results:           [[path: 'target/allure-results']],
                        commandline:       'allure'
                ])

                publishHTML([
                        allowMissing:          true,
                        alwaysLinkToLastBuild: true,
                        keepAll:               true,
                        reportDir:             'target/surefire-reports',
                        reportFiles:           'index.html',
                        reportName:            'API Test Reports'
                ])

                archiveArtifacts(
                        artifacts:         'target/surefire-reports/**/*.xml, target/allure-report/**',
                        allowEmptyArchive: true
                )

                script {
                    def testResults    = junit testResults: '**/target/surefire-reports/*.xml',
                            allowEmptyResults: true
                    env.TEST_TOTAL   = "${testResults.totalCount}"
                    env.TEST_PASSED  = "${testResults.passCount}"
                    env.TEST_FAILED  = "${testResults.failCount}"
                    env.TEST_SKIPPED = "${testResults.skipCount}"

                    def fileList    = sh(returnStdout: true,
                            script: 'ls target/surefire-reports/TEST-*.xml 2>/dev/null || true').trim()
                    def failedItems = []
                    if (fileList) {
                        for (filePath in fileList.split('\n')) {
                            def content = readFile(filePath.trim())
                            def matcher = (content =~ /<testcase name="([^"]*)"[^>]*>[\s\S]*?<failure message="([^"]*)"/)
                            while (matcher.find()) {
                                failedItems.add(" - ${matcher.group(1)}: ${matcher.group(2)}")
                            }
                        }
                    }
                    env.TEST_FAILURE_LIST = failedItems
                            ? failedItems.join("\n")
                            : " - None (All tests passed)"
                }
            }
            post {
                always {
                    script {
                        if (params.SEND_NOTIFICATIONS) {
                            def status = currentBuild.currentResult ?: 'SUCCESS'
                            def color  = (status == 'SUCCESS') ? 'good'
                                    : (status == 'UNSTABLE' ? 'warning' : 'danger')

                            def slackMsg = "*FakeStore API Tests — ${status} [Build ${env.BUILD_NUMBER}]*\n" +
                                    "Branch: *${env.BRANCH_NAME ?: 'main'}* | Author: *${env.GIT_AUTHOR ?: 'N/A'}*\n" +
                                    "Total: *${env.TEST_TOTAL ?: '0'}* | Passed: *${env.TEST_PASSED ?: '0'}* | Failed: *${env.TEST_FAILED ?: '0'}* | Skipped: *${env.TEST_SKIPPED ?: '0'}*\n\n" +
                                    "*Failed test(s) and why:*\n${env.TEST_FAILURE_LIST}\n\n" +
                                    "Build URL: ${env.BUILD_URL}\n" +
                                    "Allure Report: ${env.BUILD_URL}allure/"

                            slackSend(channel: '#jenkins_ci_report', color: color, message: slackMsg, botUser: true)

                            emailext(
                                    subject:   "[Jenkins] FakeStore Tests ${status} — Build #${env.BUILD_NUMBER}",
                                    to:        env.EMAIL_TO,
                                    mimeType:  'text/html',
                                    attachLog: true,
                                    body: "<h3>FakeStore API Tests — ${status}</h3>" +
                                            "<p><b>Job:</b> ${env.JOB_NAME} #${env.BUILD_NUMBER}</p>" +
                                            "<p><b>Branch:</b> ${env.BRANCH_NAME ?: 'main'} | <b>Author:</b> ${env.GIT_AUTHOR ?: 'N/A'}</p>" +
                                            "<p><b>Results:</b> Total: ${env.TEST_TOTAL ?: 0} | Passed: ${env.TEST_PASSED ?: 0} | Failed: ${env.TEST_FAILED ?: 0} | Skipped: ${env.TEST_SKIPPED ?: 0}</p>" +
                                            "<pre>${env.TEST_FAILURE_LIST}</pre>" +
                                            "<p><a href='${env.BUILD_URL}allure/'>View Allure Report</a></p>"
                            )
                        }
                    }
                    cleanWs()
                }
            }
        }
    }
}