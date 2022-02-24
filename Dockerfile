FROM gradle:latest as build
COPY . /home/gradle/app/
COPY src/ /home/gradle/app/src/
COPY build.gradle /home/gradle/app/build.gradle
WORKDIR /home/gradle/app/
RUN gradle bootjar

FROM openjdk:latest as runtime
EXPOSE 8081
WORKDIR /app/
COPY --from=build /home/gradle/app/build/libs/*.jar /app/spring-boot-application.jar
CMD java -jar /app/spring-boot-application.jar
