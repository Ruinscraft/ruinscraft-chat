FROM centos:7

RUN yum update -y
RUN yum install java-1.8.0-openjdk maven wget -y
RUN yum clean all

RUN mkdir /ruinscraft-chat
RUN mkdir /ruinscraft-chat/build
RUN mkdir /ruinscraft-chat/server
RUN mkdir /ruinscraft-chat/server/plugins

# Build the plugin
WORKDIR /ruinscraft-chat/build
COPY . .
RUN mvn clean
RUN mvn package
RUN cp ./bukkit/target/ruinscraft-chat.jar /ruinscraft-chat/server/plugins

# Setup Paper server
WORKDIR /ruinscraft-chat/server
RUN wget https://papermc.io/api/v1/paper/1.15.2/latest/download -O paper.jar
CMD java -Dcom.mojang.eula.agree=true -jar paper.jar --nogui

EXPOSE 25565
