package event.rec.service.config;

import event.rec.service.entities.OrganizerEntity;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Long> findUserProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    public <T> ConsumerFactory<String, T> createConsumerFactory(Class<T> userClass) {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(userClass));
    }

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return props;
    }

    @Value("${kafka.topics.find.by.id.request}")
    private String findOrganizerRequestTopic;

    @Value("${kafka.topics.find.by.id.response}")
    private String findOrganizerReplyTopic;

    @Bean
    public ReplyingKafkaTemplate<String, Long, OrganizerEntity> findOrganizerTemplate(
            ProducerFactory<String, Long> organizerFindProducerFactory) {

        ConsumerFactory<String, OrganizerEntity> organizerConsumerFactory =
                createConsumerFactory(OrganizerEntity.class);

        ConcurrentMessageListenerContainer<String, OrganizerEntity> replyContainer =
                replyContainer(organizerConsumerFactory, "auth-group", findOrganizerReplyTopic);

        return new ReplyingKafkaTemplate<>(organizerFindProducerFactory, replyContainer) {{
            setDefaultTopic(findOrganizerRequestTopic);
        }};
    }

    private <R> ConcurrentMessageListenerContainer<String, R> replyContainer(
            ConsumerFactory<String, R> consumerFactory,
            String groupId,
            String replyTopic) {

        ContainerProperties containerProperties = new ContainerProperties(replyTopic);
        containerProperties.setGroupId(groupId);

        return new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);
    }
}
