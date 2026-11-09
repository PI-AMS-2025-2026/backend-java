# syntax=docker/dockerfile:1

# ---------------------------------------------------------------------------
# Estágio 1: build da aplicação com o Maven Wrapper
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk AS build

WORKDIR /build

# Copia primeiro apenas os arquivos de build. Assim, enquanto o pom.xml não
# mudar, o Docker reaproveita a camada de download das dependências.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw

# O cache mount mantém o repositório local do Maven (~/.m2) entre builds,
# evitando rebaixar todas as dependências a cada execução.
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B dependency:go-offline

# Só agora o código-fonte entra: alterações em src/ não invalidam o cache acima.
COPY src/ src/

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -DskipTests package

# Normaliza o nome do artefato. O glob *.jar não casa com *.jar.original,
# portanto apenas o JAR executável é copiado.
RUN cp target/*.jar /build/app.jar

# ---------------------------------------------------------------------------
# Estágio 2: imagem de execução (apenas JRE, sem Maven nem código-fonte)
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime

# Usuário sem privilégios para executar a aplicação.
RUN addgroup -S -g 10001 gini \
    && adduser -S -u 10001 -G gini -h /app gini

WORKDIR /app

COPY --from=build --chown=gini:gini /build/app.jar /app/app.jar

USER gini

EXPOSE 8080

# MaxRAMPercentage respeita o limite de memória do contêiner;
# ExitOnOutOfMemoryError garante que o orquestrador reinicie o processo.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"

# start-period generoso porque a aplicação precisa subir o contexto Spring
# e aplicar as migrations do Flyway antes de responder.
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
    CMD wget -qO- http://127.0.0.1:8080/actuator/health | grep -q '"status":"UP"'

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]
