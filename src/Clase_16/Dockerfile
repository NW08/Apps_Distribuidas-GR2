FROM eclipse-temurin:21-jdk

WORKDIR /app

RUN apt-get update && \
    apt-get install -y python3 bash && \
    rm -rf /var/lib/apt/lists/*

COPY . .

RUN chmod +x mvnw
RUN chmod +x src/main/java/org/example/final2B/MapReduce/split_entrada.sh

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

ENTRYPOINT ["sh","-c","java -jar target/*.jar"]