# Temel imaj olarak OpenJDK 17 kullanıyoruz
FROM openjdk:22-jdk-slim

# Uygulamanın çalışacağı dizini belirliyoruz
WORKDIR /app

# Gradle build ile oluşturulan JAR dosyasını container içine kopyalıyoruz
COPY build/libs/*.jar /app/application.jar

# Konfigürasyon dosyalarını da container'a kopyalayın
COPY src/main/resources/servers.yaml /app/src/main/resources/servers.yaml

# Uygulamanın çalıştırılacağı komutu belirtiyoruz
ENTRYPOINT ["java", "-jar", "/app/application.jar"]

# Ktor uygulamasının çalışacağı portu açıyoruz
EXPOSE 8082
