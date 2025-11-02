package event.rec.service.utils;

import event.rec.service.requests.RegistrationRequest;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

public class CommonUserRegistrar {

    public static <T extends RegistrationRequest> Boolean registerUserInCommon(
            String requestTopic,
            String responseTopic,
            T request,
            ReplyingKafkaTemplate<String, T, Boolean> registerTemplate)
            throws ExecutionException, InterruptedException {

        ProducerRecord<String, T> record =
                new ProducerRecord<>(requestTopic, request);

        record.headers().add(new RecordHeader(
                KafkaHeaders.REPLY_TOPIC,
                responseTopic.getBytes()
        ));

        RequestReplyFuture<String, T, Boolean> future =
                registerTemplate.sendAndReceive(record, Duration.ofSeconds(5));

        return future.get().value();
    }
}
