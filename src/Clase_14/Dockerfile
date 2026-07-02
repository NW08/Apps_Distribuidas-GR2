FROM eclipse-temurin:25-jdk
WORKDIR /app

# Copiamos el JAR al contenedor
COPY target/*.jar app.jar

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]