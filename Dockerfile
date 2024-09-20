FROM openjdk:17
ARG JAR_FILE_PATH=./build/libs/*.jar
COPY ${JAR_FILE_PATH} app.jar
ENV JASYPT_ENCRYPTOR_PASSWORD=""
ENTRYPOINT ["java", "-Djasypt.encryptor.password=${JASYPT_ENCRYPTOR_PASSWORD}","-jar", "/app.jar"]