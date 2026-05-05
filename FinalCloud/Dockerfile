# --- ЭТАП 1: СБОРКА (BUILD) ---
# Используем официальный образ Maven для сборки .jar файла
FROM maven:3.9.5-eclipse-temurin-21 AS MAVEN_BUILD

# Устанавливаем кодировку (решает проблемы с кириллицей)
ENV LANG=C.UTF-8

# Копируем только pom.xml, чтобы Docker мог закэшировать зависимости
COPY pom.xml /build/
COPY mvnw /build/
COPY .mvn /build/.mvn

# Переходим в папку сборки и скачиваем все зависимости
WORKDIR /build
RUN ./mvnw dependency:go-offline -B

# Копируем весь исходный код проекта
COPY src /build/src

# Собираем проект (создаем .jar файл)
RUN ./mvnw clean package -DskipTests

# --- ЭТАП 2: ЗАПУСК (RUNTIME) ---
# Используем легковесный образ Java для запуска приложения
FROM eclipse-temurin:21-jre-alpine

# Копируем собранный .jar файл из первого этапа в текущий образ
COPY --from=MAVEN_BUILD /build/target/*.jar app.jar

# Задаем переменную окружения для Spring (не обязательно, если уже в docker-compose)
# ENV SPRING_PROFILES_ACTIVE=prod

# Команда, которая будет выполнена при запуске контейнера
ENTRYPOINT ["java", "-jar", "/app.jar"]