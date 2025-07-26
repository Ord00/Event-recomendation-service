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
import org.springframework.kafka.core.*;
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
    public ProducerFactory<String, JwtRequest> jwtRequestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, RegistrationRequest> registrationRequestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ConsumerFactory<String, JwtResponse> jwtResponseConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(JwtResponse.class));
    }

    @Bean
    public ConsumerFactory<String, Boolean> booleanConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(Boolean.class));
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

    @Value("${kafka.signin.request}")
    private String signInRequestTopic;

    @Value("${kafka.signin.response}")
    private String signInResponseTopic;

    @Bean
    public ReplyingKafkaTemplate<String, JwtRequest, JwtResponse> signInTemplate(
            ProducerFactory<String, JwtRequest> jwtRequestProducerFactory,
            ConsumerFactory<String, JwtResponse> jwtResponseConsumerFactory) {

        ConcurrentMessageListenerContainer<String, JwtResponse> replyContainer =
                replyContainer(jwtResponseConsumerFactory, "auth-group", signInResponseTopic);

        return new ReplyingKafkaTemplate<>(jwtRequestProducerFactory, replyContainer) {{
            setDefaultTopic(signInRequestTopic);
        }};
    }

    @Value("${kafka.register.common.request}")
    private String registerCommonRequestTopic;

    @Value("${kafka.register.common.response}")
    private String registerCommonResponseTopic;

    @Bean
    public ReplyingKafkaTemplate<String, RegistrationRequest, Boolean> registerCommonTemplate(
            ProducerFactory<String, RegistrationRequest> registrationRequestProducerFactory,
            ConsumerFactory<String, Boolean> booleanConsumerFactory) {

        ConcurrentMessageListenerContainer<String, Boolean> replyContainer =
                replyContainer(booleanConsumerFactory, "register-group", registerCommonResponseTopic);

        return new ReplyingKafkaTemplate<>(registrationRequestProducerFactory, replyContainer) {{
            setDefaultTopic(registerCommonRequestTopic);
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
