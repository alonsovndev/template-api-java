# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -DskipTests -Djacoco.skip=true package

# Debian-based: the Alpine JRE image has no linux/arm64 variant.
FROM eclipse-temurin:17-jre AS runtime
RUN groupadd --gid 999 appgroup && useradd --uid 999 --gid 999 --no-create-home --shell /usr/sbin/nologin appuser
WORKDIR /app
COPY --from=build /workspace/target/template-api-java.jar /app/app.jar
USER 999:999
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=3 \
    CMD ["bash", "-c", "exec 3<>/dev/tcp/127.0.0.1/8080 && printf 'GET /health/ready HTTP/1.0\\r\\n\\r\\n' >&3 && head -n 1 <&3 | grep -q ' 200 '"]
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Build with `--target lambda` for AWS Lambda Web Adapter deployments.
FROM runtime AS lambda
COPY --from=public.ecr.aws/awsguru/aws-lambda-adapter:0.9.1 /lambda-adapter /opt/extensions/lambda-adapter
ENV AWS_LWA_PORT=8080 \
    AWS_LWA_READINESS_CHECK_PATH=/health/ready

# Last stage stays the plain runtime so the default target never carries the adapter.
FROM runtime
