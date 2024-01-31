FROM flink:latest

#RUN wget https://repo1.maven.org/maven2/org/apache/flink/flink-connector-kafka_2.12/1.14.6/flink-connector-kafka_2.12-1.14.6.jar -P /opt/flink/lib
#RUN wget https://repo1.maven.org/maven2/org/apache/kafka/kafka-clients/3.5.1/kafka-clients-3.5.1.jar -P /opt/flink/lib
#RUN wget https://repo1.maven.org/maven2/io/circe/circe-core_2.12/0.12.3/circe-core_2.12-0.12.3.jar -P /opt/flink/li
#RUN wget https://repo1.maven.org/maven2/io/circe/circe-generic_2.12/0.15.0-M1/circe-generic_2.12-0.15.0-M1.jar -P /opt/flink/lib
#RUN wget https://repo1.maven.org/maven2/io/circe/circe-parser_2.12/0.10.1/circe-parser_2.12-0.10.1.jar -P /opt/flink/lib
#RUN wget https://repo1.maven.org/maven2/io/circe/circe-jawn_2.12/0.14.3/circe-jawn_2.12-0.14.3.jar -P /opt/flink/lib
#RUN wget https://repo1.maven.org/maven2/org/typelevel/jawn-parser_2.12/1.2.0/jawn-parser_2.12-1.2.0.jar -P /opt/flink/lib
#RUN wget https://repo1.maven.org/maven2/org/typelevel/cats-core_2.12/1.2.0/cats-core_2.12-1.2.0.jar -P /opt/flink/lib

RUN mkdir -p /opt/flink/data

RUN chmod 775 /opt/flink/data

COPY flink-conf.yaml /opt/flink/conf/

RUN apt-get update && \
    apt-get install -y \
        wget \
        unzip

ENV SCALA_VERSION 2.13.1
ENV SBT_VERSION 0.13.17

# Install sbt
RUN apt-get update && \
    apt-get install -y curl && \
    curl -L "https://github.com/sbt/sbt/releases/download/v1.5.5/sbt-1.5.5.tgz" -o sbt.tgz && \
    tar -zxvf sbt.tgz -C /usr/share/ && \
    ln -s /usr/share/sbt/bin/sbt /usr/bin/sbt && \
    rm sbt.tgz

COPY . .

#WORKDIR event-processor

RUN sbt clean assembly

#RUN cp /opt/flink/event-processor/target/scala-2.12/event-processor-assembly-0.1.0-SNAPSHOT.jar /opt/flink/lib/event-processor-assembly-0.1.0-SNAPSHOT.jar

WORKDIR $FLINK_HOME