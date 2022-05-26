FROM openjdk:11-jre-slim
# create non-root user and group inside container
RUN groupadd -g 10599 apps
RUN useradd -u 10599 -g apps apps
# Planetway internal CA certificates
COPY src/main/resources/pw_rca.pem /usr/local/share/ca-certificates/pw_rca.crt
COPY src/main/resources/pw_ica.pem /usr/local/share/ca-certificates/pw_ica.crt
RUN update-ca-certificates
# use non-root user inside container
USER apps
EXPOSE 8082
COPY ./build/libs/*.jar /opt/planetid/relying_party_fudosan/relyingparty-mock.jar
WORKDIR /opt/planetid/relying_party_fudosan
CMD ["java", "-jar", "relyingparty-mock.jar"]
