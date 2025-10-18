node("ci-node"){
    def GIT_COMMIT_HASH = ""

    stage("Checkout"){
        checkout scm
        GIT_COMMIT_HASH = sh (script: "git log -n 1 --pretty=format:'%H'", returnStdout: true)
    }

    stage("Unit tests"){
        sh "chmod 777 mvnw && ./mvnw clean test"
    }


    stage("Build Jar file"){
        sh "./mvnw package -DskipTests"
    }

    stage("Build Docker Image"){
        sh "sudo docker build -t mchekini/scpi-inv-api:$GIT_COMMIT_HASH ."
    }

    stage("Push Docker image"){
        withCredentials([usernamePassword(credentialsId: 'mchekini', passwordVariable: 'password', usernameVariable: 'username')]) {
            sh "sudo docker login -u $username -p $password"
            sh "sudo docker push mchekini/scpi-inv-api:$GIT_COMMIT_HASH"
            sh "sudo docker rmi mchekini/scpi-inv-api:$GIT_COMMIT_HASH"
        }
    }
}