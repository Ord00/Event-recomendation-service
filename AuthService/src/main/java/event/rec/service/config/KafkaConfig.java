package event.rec.service.config;

import event.rec.service.requests.AdminRegistrationRequest;
import event.rec.service.requests.CommonUserRegistrationRequest;
import event.rec.service.requests.JwtRequest;
import event.rec.service.requests.OrganizerRegistrationRequest;
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

    @Bean
    public ProducerFactory<String, JwtRequest> jwtRequestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public <T extends RegistrationRequest> ProducerFactory<String, T> registrationRequestProducerFactory() {
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
    public ReplyingKafkaTemplate<String, CommonUserRegistrationRequest, Boolean> registerCommonTemplate(
            ProducerFactory<String, CommonUserRegistrationRequest> registrationRequestProducerFactory,
            ConsumerFactory<String, Boolean> booleanConsumerFactory) {

        ConcurrentMessageListenerContainer<String, Boolean> replyContainer =
                replyContainer(booleanConsumerFactory, "register-group", registerCommonResponseTopic);

        return new ReplyingKafkaTemplate<>(registrationRequestProducerFactory, replyContainer) {{
            setDefaultTopic(registerCommonRequestTopic);
        }};
    }

    @Value("${kafka.register.organizer.request}")
    private String registerOrganizerRequestTopic;

    @Value("${kafka.register.organizer.response}")
    private String registerOrganizerResponseTopic;

    @Bean
    public ReplyingKafkaTemplate<String, OrganizerRegistrationRequest, Boolean> registerOrganizerTemplate(
            ProducerFactory<String, OrganizerRegistrationRequest> registrationRequestProducerFactory,
            ConsumerFactory<String, Boolean> booleanConsumerFactory) {

        ConcurrentMessageListenerContainer<String, Boolean> replyContainer =
                replyContainer(booleanConsumerFactory, "register-group", registerOrganizerResponseTopic);

        return new ReplyingKafkaTemplate<>(registrationRequestProducerFactory, replyContainer) {{
            setDefaultTopic(registerOrganizerRequestTopic);
        }};
    }

    @Value("${kafka.register.admin.request}")
    private String registerAdminRequestTopic;

    @Value("${kafka.register.admin.response}")
    private String registerAdminResponseTopic;

    @Bean
    public ReplyingKafkaTemplate<String, AdminRegistrationRequest, Boolean> registerAdminTemplate(
            ProducerFactory<String, AdminRegistrationRequest> registrationRequestProducerFactory,
            ConsumerFactory<String, Boolean> booleanConsumerFactory) {

        ConcurrentMessageListenerContainer<String, Boolean> replyContainer =
                replyContainer(booleanConsumerFactory, "register-group", registerAdminResponseTopic);

        return new ReplyingKafkaTemplate<>(registrationRequestProducerFactory, replyContainer) {{
            setDefaultTopic(registerAdminRequestTopic);
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
