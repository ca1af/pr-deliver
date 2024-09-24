FROM openjdk:17
# JDK17 사용
ARG JAR_FILE_PATH=./build/libs/*.jar
# gradle build 후 생성되는 jar 파일
COPY ${JAR_FILE_PATH} app.jar
# jar 파일을 app.jar 파일로 카피
ENV JASYPT_ENCRYPTOR_PASSWORD=""
#JASYPT 패스워드를 명시적으로 "" 선언함으로써, 변수가 주입되지 않으면 빈 문자열이 주입
ENTRYPOINT ["java", "-Djasypt.encryptor.password=${JASYPT_ENCRYPTOR_PASSWORD}","-jar", "/app.jar"]
# "-Djasypt.encryptor.password=${JASYPT_ENCRYPTOR_PASSWORD}" Jasypt 패스워드 주입 (Github Actions)