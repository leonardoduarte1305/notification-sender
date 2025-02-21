### Desenvolvedores:

- [Leonardo Gonçalves Duarte da Silva](https://www.linkedin.com/in/leonardoduarte1305/) - leonardoduarte1305@gmail.com

<hr>

# Notification Sender - Overview

### Módulo notification-sender-spec

#### - Define os endpoints para os respectivos envios

#### - Recurso  principal: [api-spec](./notification-sender-spec/src/main/resources/api-specification.yaml)

### Módulo notification-sender-service

#### - Implementa os endpoints definidos na especificação e dá outras opções

### [Ao adicionar novos recursos preenchê-los aqui]

<hr>

## Instruções de build

### Empacota

```
mvn clean package
```

### Empacota, gera imagem de contêiner e envia para o DockerHub a imagem gerada

A utilização do profile "docker-build-push" surte efeito apenas no módulo de serviço
(Deve-se tomar cuidado neste passo porque uma imagem criada no MacOS (arm64)
não é compatível para rodar no Linux (amd64))

Solução encontrada para este problema: executar este passo em uma máquina Linux

````
mvn clean package -Pdocker-build-push
````

## Instruções de start local

Para rodar o projeto localmente utilize o seguinte comando
(nesta modalidade o sistemas irá apenas logar os envios)

```
env $(cat exemplo.env | xargs) mvn spring-boot:run
```

## Instruções de start com docker

```
docker run \
--rm \
-it \
--name notification-sender \
-p 8080:8080 \
--env-file /$HOME/PATH_TO_ENV_FILE/.env \
docker.io/leonardoduarte1305/notification-sender-service:0.0.1-SNAPSHOT
```

## Instruções para docker-compose

    services:
        notification-sender:
        image: leonardoduarte1305/notification-sender-service:0.0.1-SNAPSHOT
        container_name: notification-sender-compose
        ports:
          - "8010:8080"
        env_file:
          - .env

## Instruções de deploy

### As variáveis de ambiente necessárias são as seguintes:

    SERVER_PORT=8080
    SPRING_MAIL_HOST=smtp.email.com
    SPRING_MAIL_PORT=587
    SPRING_MAIL_USERNAME=username
    SPRING_MAIL_PASSWORD=senha
    SPRING_PROFILES_ACTIVE=local
    X_API_KEY=ApiKeyAceitaPeloServicoDeEmail