FROM ubuntu:focal
ENV DEBIAN_FRONTEND=noninteractive

RUN apt update && apt upgrade -y
RUN apt install wget -y
RUN apt install build-essential -y
RUN apt install openjdk-17-jdk-headless -y
RUN apt install python3 -y
RUN apt install git -y

RUN wget https://github.com/JetBrains/kotlin/releases/download/v1.7.20/kotlin-native-linux-x86_64-1.7.20.tar.gz -O kotlin.tar.gz && tar -xzf kotlin.tar.gz && rm kotlin.tar.gz
RUN wget 'https://sourceforge.net/projects/xsb/files/xsb/5.0%20%28Green%20Tea%29/XSB-5.0.tar.gz/download' -O xsb.tar.gz && tar -xzf xsb.tar.gz && rm xsb.tar.gz
RUN wget https://dlcdn.apache.org/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz -O maven.tar.gz && tar -xzf maven.tar.gz && rm maven.tar.gz

RUN apt install graphviz -y
RUN apt install make -y
RUN apt install mysql-client -y
RUN apt install flex -y && apt install bison -y

WORKDIR /XSB/build
RUN ./configure
RUN ./makexsb
WORKDIR /

ENV M2_HOME="/apache-maven-3.8.6"
ENV MAVEN_HOME=${M2_HOME}

ENV PATH="${M2_HOME}/bin:/XSB/bin:/kotlin-native-linux-x86_64-1.7.20/bin:$PATH"

