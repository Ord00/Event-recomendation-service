package event.rec.service.config;

import event.rec.service.requests.JwtRequest;
import event.rec.service.requests.RegistrationRequest;
import event.rec.service.responses.JwtResponse;
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

    @Value("${kafka.signin.request}")
    private String signInRequestTopic;

    @Value("${kafka.signin.response}")
    private String signInResponseTopic;

    @Bean
    public ReplyingKafkaTemplate<String, JwtRequest, JwtResponse> signInTemplate(
            ProducerFactory<String, JwtRequest> producerFactory,
            ConsumerFactory<String, JwtResponse> consumerFactory) {

        ConcurrentMessageListenerContainer<String, JwtResponse> replyContainer =
                replyContainer(consumerFactory, "auth-group", signInResponseTopic);

        ReplyingKafkaTemplate<String, JwtRequest, JwtResponse> template =
                new ReplyingKafkaTemplate<>(producerFactory, replyContainer);

        template.setDefaultTopic(signInRequestTopic);
        return template;
    }

    private <R> ConcurrentMessageListenerContainer<String, R> replyContainer(
            ConsumerFactory<String, R> consumerFactory,
            String groupId,
            String replyTopic) {

        ContainerProperties containerProperties = new ContainerProperties(replyTopic);
        containerProperties.setGroupId(groupId);

        return new ConcurrentMessageListenerContainer<>(
                consumerFactory,
                containerProperties
        );
    }

    @Bean
    public ProducerFactory<String, JwtRequest> jwtRequestProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ConsumerFactory<String, JwtResponse> jwtResponseConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "event.rec.service.responses");
        return new DefaultKafkaConsumerFactory<>(config,
                new StringDeserializer(),
                new JsonDeserializer<>(JwtResponse.class));
    }

    @Value("${kafka.register.common.request}")
    private String registerCommonRequestTopic;

    @Value("${kafka.register.common.response}")
    private String registerCommonResponseTopic;

    @Bean
    public ReplyingKafkaTemplate<String, RegistrationRequest, Boolean> registerCommonTemplate(
            ProducerFactory<String, RegistrationRequest> producerFactory,
            ConsumerFactory<String, Boolean> consumerFactory) {

        ConcurrentMessageListenerContainer<String, Boolean> replyContainer =
                replyContainer(consumerFactory, "register-group", registerCommonResponseTopic);

        ReplyingKafkaTemplate<String, RegistrationRequest, Boolean> template =
                new ReplyingKafkaTemplate<>(producerFactory, replyContainer);

        template.setDefaultTopic(registerCommonRequestTopic);
        return template;
    }
}
