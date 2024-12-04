#!groovy
import groovy.json.JsonSlurperClassic
node {

    def BUILD_NUMBER = env.BUILD_NUMBER
    def RUN_ARTIFACT_DIR="tests/${BUILD_NUMBER}"
    def SFDC_USERNAME

    def HUB_ORG = 'astou@dev2.sandbox'   /*env.HUB_ORG_DH_Jenkins*/
    def SFDC_HOST = 'https://login.salesforce.com' /*env.SFDC_HOST_DH_Jenkins*/
    def JWT_KEY_CRED_ID = 'e0f49593-c817-4157-a2d0-01eafb193261' /*env.JWT_CRED_ID_DH_Jenkins*/
    def CONNECTED_APP_CONSUMER_KEY = '3MVG9k02hQhyUgQB4w7s4Y1CJFCYW7IO_WXwlRmVzzl2OdOXttTaD._rEpsV_pUPP75n2FQH_3JcMito7yLV9' /*env.CONNECTED_APP_CONSUMER_KEY_DH_Jenkins*/
    def jwt_key_file = './bin/server.key'
    def sfdxPath = 'C:\Program Files\sf\client\bin\sfdx' //'c:/Program Files/sf/bin/sfdx'

    println 'KEY IS' 
    println JWT_KEY_CRED_ID
    println 'USERNAME IS'
    println HUB_ORG
    println 'INSTANCE URL IS'
    println SFDC_HOST
    println 'CONNECTED APP KEY IS'
    println CONNECTED_APP_CONSUMER_KEY
    println 'servey.key PATH IS'
    println jwt_key_file

    // def toolbelt = tool 'toolbelt'

    

    withCredentials([file(credentialsId: JWT_KEY_CRED_ID, variable: 'jwt_key_file')]) {
        // stage('Check SFDX Path') {
        //     bat 'sfdx --version'
        // }
        stage('Deploye Code') {
            if (isUnix()) {
                rc = sh returnStatus: true, script: "${sfdxPath} force:auth:jwt:grant --clientid ${CONNECTED_APP_CONSUMER_KEY} --username ${HUB_ORG} --jwtkeyfile ${jwt_key_file} --setdefaultdevhubusername --instanceurl ${SFDC_HOST}"
            }else{
                 rc = bat returnStatus: true, script: "\" ${sfdxPath}\" force:auth:jwt:grant --clientid ${CONNECTED_APP_CONSUMER_KEY} --username ${HUB_ORG} --jwtkeyfile \"${jwt_key_file}\" --setdefaultdevhubusername --instanceurl ${SFDC_HOST}"
            }
            // if (isUnix()) {
            //     rc = sh returnStatus: true, script: "${toolbelt} force:auth:jwt:grant --clientid ${CONNECTED_APP_CONSUMER_KEY} --username ${HUB_ORG} --jwtkeyfile ${jwt_key_file} --setdefaultdevhubusername --instanceurl ${SFDC_HOST}"
            // }else{
            //      rc = bat returnStatus: true, script: "\"${toolbelt}\" force:auth:jwt:grant --clientid ${CONNECTED_APP_CONSUMER_KEY} --username ${HUB_ORG} --jwtkeyfile \"${jwt_key_file}\" --setdefaultdevhubusername --instanceurl ${SFDC_HOST}"
            // }
            // if (rc != 0) { 
            //     error 'hub org authorization failed' 
            // }

			println rc
			
			// need to pull out assigned username
			if (isUnix()) {
				rmsg = sh returnStdout: true, script: "sfdx force:mdapi:deploy -d manifest/package.xml -u ${HUB_ORG}"
			}else{
			   rmsg = bat returnStdout: true, script: "\"sfdx\" force:mdapi:deploy -d manifest/package.xml -u ${HUB_ORG}"
			}
			  
            printf rmsg
            println('Hello from a Job DSL script!')
            println(rmsg)
        }
    }
}