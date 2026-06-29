### Desenvolvedores:

- [Leonardo Gonçalves Duarte da Silva](https://www.linkedin.com/in/leonardoduarte1305/) - leonardoduarte1305@gmail.com

<hr>

# Notification Sender - Overview

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

    SPRING_MAIL_HOST=smtp.gmail.com
    SPRING_MAIL_PORT=587
    SPRING_MAIL_USERNAME=username_secreto
    SPRING_MAIL_PASSWORD=senha_super_secreta
    
    KAFKA_BOOTSTRAP_SERVERS=kafka-broker-1:19092,kafka-broker-2:19092,kafka-broker-3:19092
    
    POSTGRES_HOST=postgres
    POSTGRES_PORT=5432
    POSTGRES_DB=schema_do_banco
    POSTGRES_USER=user_do_banco
    POSTGRES_PASSWORD=senha_do_banco
    
    GROUP_ID=notification-sender
    TOPICO_ENVIO_EMAIL=topico-correto-para-envio-de-email
    TOPICO_ENVIO_EMAIL_DLQ=topico-correto-para-envio-de-email-dlq
    KAFKA_AUTO_CREATE_TOPICS=true