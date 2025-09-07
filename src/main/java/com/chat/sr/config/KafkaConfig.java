package com.chat.sr.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;



//brew services start kafka
// .\bin\windows\kafka-topics.bat --create --topic chat.messages --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
//netstat -an | grep 9092
//kafka-topics --create --topic online.presence --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
//kafka-console-producer --topic online.presence --bootstrap-server localhost:9092
//kafka-console-consumer --topic online.presence --bootstrap-server localhost:9092 --from-beginning --max-messages 5

//kafka-topics --create --topic chat.messages --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
//kafka-console-producer --topic chat.messages --bootstrap-server localhost:9092
//kafka-console-consumer --topic chat.messages --bootstrap-server localhost:9092 --from-beginning --max-messages 5
@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic presenceTopic() {
        return TopicBuilder.name("online.presence").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic chatTopic() {
        return TopicBuilder.name("chat.messages").partitions(3).replicas(1).build();
    }
}
