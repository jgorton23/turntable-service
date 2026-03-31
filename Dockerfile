FROM public.ecr.aws/docker/library/gradle:8.12-jdk21-alpine AS build
WORKDIR /app
COPY . .
RUN gradle :app:shadowJar -x test -q

FROM public.ecr.aws/docker/library/eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/app/build/libs/app-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
