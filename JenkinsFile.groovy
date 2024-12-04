#!groovy
import groovy.json.JsonSlurperClassic

node {
    // Initialisation des variables
    def BUILD_NUMBER = env.BUILD_NUMBER
    def RUN_ARTIFACT_DIR = "tests/${BUILD_NUMBER}"
    def SFDC_USERNAME

    def HUB_ORG = 'astou@dev2.sandbox' // Nom d'utilisateur Salesforce
    def SFDC_HOST = 'https://login.salesforce.com' // URL de l'instance Salesforce
    def JWT_KEY_CRED_ID = 'e0f49593-c817-4157-a2d0-01eafb193261' // ID des informations d'identification JWT
    def CONNECTED_APP_CONSUMER_KEY = '3MVG9k02hQhyUgQB4w7s4Y1CJFCYW7IO_WXwlRmVzzl2OdOXttTaD._rEpsV_pUPP75n2FQH_3JcMito7yLV9' // Clé consommateur de l'application connectée
    def jwt_key_file = './bin/server.key' // Chemin du fichier de clé JWT

    // Affichage des informations pour vérification
    println "KEY IS: ${JWT_KEY_CRED_ID}"
    println "USERNAME IS: ${HUB_ORG}"
    println "INSTANCE URL IS: ${SFDC_HOST}"
    println "CONNECTED APP KEY IS: ${CONNECTED_APP_CONSUMER_KEY}"
    println "JWT KEY FILE PATH IS: ${jwt_key_file}"

    // Utilisation des informations d'identification Jenkins
    withCredentials([file(credentialsId: JWT_KEY_CRED_ID, variable: 'jwt_key_file')]) {
        stage('Deploy Code') {
            // Authentification via JWT avec Salesforce CLI
            def rc
            if (isUnix()) {
                rc = sh returnStatus: true, script: """
                    sfdx force:auth:jwt:grant \
                        --clientid ${CONNECTED_APP_CONSUMER_KEY} \
                        --username ${HUB_ORG} \
                        --jwtkeyfile ${jwt_key_file} \
                        --setdefaultdevhubusername \
                        --instanceurl ${SFDC_HOST}
                """
            } else {
                rc = bat returnStatus: true, script: """
                    "sfdx" force:auth:jwt:grant \
                        --clientid ${CONNECTED_APP_CONSUMER_KEY} \
                        --username ${HUB_ORG} \
                        --jwtkeyfile "${jwt_key_file}" \
                        --setdefaultdevhubusername \
                        --instanceurl ${SFDC_HOST}
                """
            }
            
            println "Authentication exit code: ${rc}"

            // Déploiement du code avec Salesforce CLI
            def rmsg
            if (isUnix()) {
                rmsg = sh returnStdout: true, script: """
                    sfdx force:mdapi:deploy \
                        -d manifest/package.xml \
                        -u ${HUB_ORG}
                """
            } else {
                rmsg = bat returnStdout: true, script: """
                    "sfdx" force:mdapi:deploy \
                        -d manifest/package.xml \
                        -u ${HUB_ORG}
                """
            }

            // Affichage du message de réponse
            println "Deployment result message:"
            println rmsg

            // Message final
            println('Deployment complete!')
        }
    }
}
