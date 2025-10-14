FROM ubuntu:latest

RUN apt update -y && apt install openjdk-17-jre -y && apt install postgresql -y

# Switch to the postgres user and create a new user
USER postgres

# Initialize the database and start PostgreSQL service
RUN /etc/init.d/postgresql start && \
     psql --command "CREATE USER inventory WITH PASSWORD 'invent';" && \
     psql --command "ALTER USER inventory WITH SUPERUSER;" && \
     psql --command "CREATE DATABASE assessment  WITH OWNER = inventory;" &&\
    /etc/init.d/postgresql stop

#switching to root
USER root

WORKDIR /javaApplication

COPY build/libs/Apis.jar .

CMD service postgresql start && java -jar Apis.jar


#  To make the jar
#  chmod +x ./gradlew
#  ./gradlew clean build
#  ./gradlew clean build --refresh-dependencies


# In order to build the docker image run this command
# docker build -t Apis .

# In order to run the docker container
# docker run --name spring -p8080:8080 Apis