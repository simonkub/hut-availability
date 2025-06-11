# --- Phase 1: Builder ---
# Wir starten mit einem vollen JDK (Java Development Kit), um die Anwendung zu bauen.
FROM eclipse-temurin:17-jdk-jammy as builder

WORKDIR /app

# Kopiere das gesamte Projekt in den Builder
COPY . .

# Führe den Gradle-Befehl aus, um die "Fat Jar" zu erstellen.
# Dieser Task kommt vom "shadow"-Plugin.
RUN ./gradlew shadowJar


# --- Phase 2: Runner ---
# Wir starten mit einem schlanken JRE (Java Runtime Environment), um die Anwendung auszuführen.
# Das macht das finale Image viel kleiner.
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Kopiere NUR die gebaute "Fat Jar" aus der Builder-Phase.
# Der Name der Jar wird typischerweise "-all.jar" am Ende haben.
COPY --from=builder /app/build/libs/*-all.jar ./app.jar

# Definiere den Befehl, der beim Starten des Containers ausgeführt wird.
CMD ["java", "-jar", "app.jar"]