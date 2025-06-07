package org.example.Common.config;

import org.example.Common.DTO.TransactionAsseptedMessage;
import org.example.Common.DTO.TransactionResultMessage;
import org.example.Common.DTO.TransactionSendMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

//файл конфигурации для сообщений
@Configuration
@EnableKafka
@Component
public class KafkaConfig {

    //бин для сообщений, которые будут отправлены
    @Bean
    public ProducerFactory<String, TransactionSendMessage> producerFactory() {
        Map<String, Object> prod = new HashMap<>();
        //Адрес Kafka брокера
        prod.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //Сериализатор ключа сообщения(определяет классс который превращает сообщение в байт объект для передачи, определяет строковый ключ)
        prod.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
        //Сериализатор значения сообщения(объект TransactionAcceptMessage будет автоматически преобразован в JSON.)
        prod.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.JsonSerializer.class);
        //Создаём фабрику с указанными настройками(Spring будет использовать эту фабрику для создания продьюсеров при отправке сообщений через KafkaTemplate)
        return new DefaultKafkaProducerFactory<>(prod);
    }

    @Bean
    public KafkaTemplate<String, TransactionSendMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    //бин для сообщений, которые будут отправлены
    @Bean
    public ProducerFactory<String, TransactionResultMessage> producerResultFactory() {
        Map<String, Object> prod1 = new HashMap<>();
        //Адрес Kafka брокера
        prod1.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //Сериализатор ключа сообщения(определяет классс который превращает сообщение в байт объект для передачи, определяет строковый ключ)
        prod1.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
        //Сериализатор значения сообщения(объект TransactionAcceptMessage будет автоматически преобразован в JSON.)
        prod1.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.JsonSerializer.class);
        //Создаём фабрику с указанными настройками(Spring будет использовать эту фабрику для создания продьюсеров при отправке сообщений через KafkaTemplate)
        return new DefaultKafkaProducerFactory<>(prod1);
    }

    @Bean
    public KafkaTemplate<String, TransactionResultMessage> kafkaResultTemplate() {
        return new KafkaTemplate<>(producerResultFactory());
    }

    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaStringTemplate() {
        return new KafkaTemplate<>(stringProducerFactory());
    }



    @Bean
    public ConsumerFactory<String, TransactionAsseptedMessage> consumerFactory() {
        Map<String, Object> consume = new HashMap<>();
        consume.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        consume.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consume.put(ConsumerConfig.GROUP_ID_CONFIG, "common-service-group"); //GROUP_ID_CONFIG — группа потребителей, которая обеспечивает балансировку и контроль оффсетов.
        consume.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class);
        consume.put(JsonDeserializer.VALUE_DEFAULT_TYPE, TransactionAsseptedMessage.class);
        consume.put(JsonDeserializer.TRUSTED_PACKAGES, "org.example.Common.DTO");
        consume.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.JsonDeserializer.class);
       //TRUSTED_PACKAGES — указывает, из каких пакетов можно десериализовывать объекты
        return new DefaultKafkaConsumerFactory<>(consume, new StringDeserializer(), new JsonDeserializer<>(TransactionAsseptedMessage.class));
        //создаётся DefaultKafkaConsumerFactory с этими настройками и указанием, что ключ десериализуется строкой, а значение — объектом TransactionMessage.
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionAsseptedMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionAsseptedMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, TransactionResultMessage> transactionResultConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "common-service-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "org.example.Common.DTO");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, TransactionResultMessage.class);
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(TransactionResultMessage.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionResultMessage> transactionResultKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionResultMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(transactionResultConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, TransactionSendMessage> transactionSendMessageConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "common-service-group"); // или ваша группа
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "org.example.Common.DTO");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, TransactionSendMessage.class);
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(TransactionSendMessage.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionSendMessage> transactionSendMessageKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionSendMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(transactionSendMessageConsumerFactory());
        return factory;
    }

}
