FROM ubuntu:18.04

RUN apt-get update

RUN apt-get install -y wget

#Java Setup
RUN apt-get update --fix-missing
RUN apt-get install -y default-jdk

#Maven Setup
RUN apt-get install -y maven

# Set the home directory to /root
ENV HOME /root
WORKDIR /root

# Copy all app files into the image
COPY . .

#Downloads dependencies
RUN mvn package

# Install Node
RUN apt-get update --fix-missing

# Allow port 8000 to be accessed
EXPOSE 8000

#Docker compose
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.2.1/wait /wait
RUN chmod +x /wait 

# Run the app
# RUN javac -verbose src/Webserver.java
# CMD /wait && java src/Webserver
CMD /wait && java -jar dockertest.jar 

#https://runnable.com/docker/java/dockerize-your-java-application