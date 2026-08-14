# ---------- Build stage ----------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test \
    && JAR="$(find build/libs -name '*.jar' ! -name '*-plain.jar' | head -1)" \
    && test -n "$JAR" \
    && cp "$JAR" /app/app.jar \
    && ls -l /app/app.jar

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
RUN groupadd -r spring && useradd -r -g spring spring
COPY --from=build --chown=spring:spring /app/app.jar app.jar
USER spring

# Demo deployment: H2 runs in memory, so no writable volume is needed and
# DataSeeder repopulates the quotes and demo user on every startup.
ENV SPRING_DATASOURCE_URL="jdbc:h2:mem:quizdb;DB_CLOSE_DELAY=-1"

EXPOSE 8080

# 50% leaves room for metaspace, thread stacks and native memory on a 512MB
# instance; the host supplies $PORT (Render defaults it to 10000).
ENTRYPOINT ["sh", "-c", "exec /opt/java/openjdk/bin/java -XX:MaxRAMPercentage=50 -XX:+UseSerialGC -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
