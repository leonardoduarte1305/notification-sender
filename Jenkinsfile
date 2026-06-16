pipeline {
    agent any
    tools {
        maven "maven-3"
        jdk "jdk-21"
    }
    environment {
        MAVEN = "mvn -B -Dstyle.color=always -Dmaven.test.redirectTestOutputToFile=false"
        BRANCH = 'master'
        SLACK_CHANNEL = 'jenkins_builds'
    }
    stages {
        stage("Build + Tests") {
            steps {
                sh "${MAVEN} clean verify"
            }
        }
        stage("Prepare Image Version") {
            steps {
                script {
                    env.GIT_COMMIT_SHORT = sh(
                            script: "git rev-parse --short HEAD",
                            returnStdout: true
                    ).trim()

                    env.IMAGE_VERSION = "build-${BUILD_NUMBER}-${GIT_COMMIT_SHORT}"
                }

                echo "Gerada a imagem de versão: ${IMAGE_VERSION}"
            }
        }
        stage("Build + Push Notification Image") {
            steps {
                sh "${MAVEN} clean package -Pdocker-build-push -Ddocker.tag=${IMAGE_VERSION}"
            }
        }
        stage("Update compose versions") {
            steps {
                sh """
                   sed -i 's/^NOTIFICATION_SENDER_VERSION=.*/NOTIFICATION_SENDER_VERSION=${IMAGE_VERSION}/' /deploy/server-asus/.env
                   cat /deploy/server-asus/.env
                """
            }
        }
        stage("Deploy Containers") {
            steps {
                sh """
                    docker compose \
                      --project-directory /deploy/server-asus \
                      up -d notification-sender

                    docker compose \
                      --project-directory /deploy/server-asus \
                      ps
                """
            }
        }
    }
    post("Finally") {
        always {
            sh "docker logout"
        }
        success {
            script {
                slackSend(
                        channel: SLACK_CHANNEL,
                        message: "Build #${BUILD_NUMBER} - The build finished Successfully. [${BUILD_URL}] :ok_hand:",
                )
            }
        }
        failure {
            script {
                slackSend(
                        channel: SLACK_CHANNEL,
                        message: "Build #${BUILD_NUMBER} - The build couldn't finish due something wrong. [${BUILD_URL}] :cold_face:",
                )
            }
        }
        aborted {
            script {
                slackSend(
                        channel: SLACK_CHANNEL,
                        message: "Build #${BUILD_NUMBER} - The build was aborted. [${BUILD_URL}] :zipper_mouth_face:",
                )
            }
        }
    }
}