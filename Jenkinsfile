pipeline {
  agent {
    node {
      label 'maven'
    }

  }
  environment {
    DOCKER_CREDENTIAL_ID = 'aliyunhub-id'
    GITHUB_CREDENTIAL_ID = 'git-id'
    KUBECONFIG_CREDENTIAL_ID = 'demo-kubeconfig'
    REGISTRY = 'registry.cn-hangzhou.aliyuncs.com'
    ALIYUNHUB_NAMESPACE = 'smilve'
    GITHUB_ACCOUNT = 'smilve'
    SONAR_CREDENTIAL_ID = 'sonar-qube'
    BRANCH_NAME = 'prod'
  }

  stages {
    stage('拉取代码') {
      steps {
        git(url: 'https://gitee.com/dalianpai/gulimall.git', credentialsId: 'gitee-id', branch: 'prod', changelog: true, poll: false)
        sh 'echo 正在构建 $PROJECT_NAME 版本号：$PROJECT_VERSION'
        container ('maven') {
            sh "mvn clean install -Dmaven.test.skip=true -P prod -gs `pwd`/mvn-settings.xml"
        }
      }
    }

    stage ('构建镜像-推送镜像') {
        steps {
            container ('maven') {
                sh 'mvn  -Dmaven.test.skip=true -P prod -gs `pwd`/mvn-settings.xml clean package'
                sh 'cd $PROJECT_NAME && docker build -f Dockerfile -t $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER .'
                withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
                    sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
                    sh 'docker tag  $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:latest '
                    sh 'docker push  $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:latest '
                }
            }
        }
    }

    stage('部署到k8s') {
          steps {
            input(id: "deploy-to-dev-$PROJECT_NAME", message: "是否将 $PROJECT_NAME 部署到集群中?")
            kubernetesDeploy(configs: "$PROJECT_NAME/deploy/**", enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
          }
    }

    stage('发布版本'){
          when{
            expression{
              return params.PROJECT_VERSION =~ /v.*/
            }
          }
          steps {
              container ('maven') {
                input(id: 'release-image-with-tag', message: '发布当前版本镜像吗?')
                  withCredentials([usernamePassword(credentialsId: "$GITHUB_CREDENTIAL_ID", passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                    sh 'git config --global user.email "dalianpai@163.com" '
                    sh 'git config --global user.name "dalianpai" '
                    sh 'git tag -a $PROJECT_VERSION -m "$PROJECT_VERSION" '
                    sh 'git push http://$GIT_USERNAME:$GIT_PASSWORD@gitee.com/$GITHUB_ACCOUNT/gulimall.git --tags --ipv4'
                  }
                sh 'docker tag  $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:$PROJECT_VERSION '
                sh 'docker push  $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:$PROJECT_VERSION '
          }
          }
     }

  }
  parameters {
    string(name: 'PROJECT_VERSION', defaultValue: 'v0.0Beta', description: '')
    string(name: 'PROJECT_NAME', defaultValue: '', description: '')
  }
}