package event.rec.service.utils;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;

import java.time.Duration;

public class KafkaRequestSender {

    public static RequestReplyFuture<String, String, Long> findUserId(
            String requestTopic,
            String replyTopic,
            String username,
            ReplyingKafkaTemplate<String, String, Long> findUserIdTemplate) {

        ProducerRecord<String, String> record = new ProducerRecord<>(
                requestTopic,
                username
        );

        record.headers().add(new RecordHeader(
                KafkaHeaders.REPLY_TOPIC,
                replyTopic.getBytes()
        ));

        return findUserIdTemplate.sendAndReceive(record, Duration.ofSeconds(5));
    }
}
