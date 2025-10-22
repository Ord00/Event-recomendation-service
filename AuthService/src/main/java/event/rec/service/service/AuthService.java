package event.rec.service.service;

import event.rec.service.requests.JwtRequest;
import event.rec.service.responses.JwtResponse;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ReplyingKafkaTemplate<String, JwtRequest, JwtResponse> signInTemplate;
    @Value("${kafka.signin.request}")
    private String signInRequestTopic;
    @Value("${kafka.signin.response}")
    private String signInReplyTopic;

    public JwtResponse signIn(JwtRequest jwtRequest) throws ExecutionException, InterruptedException {

        ProducerRecord<String, JwtRequest> record = new ProducerRecord<>(
                signInRequestTopic,
                jwtRequest
        );

        record.headers().add(new RecordHeader(
                KafkaHeaders.REPLY_TOPIC,
                signInReplyTopic.getBytes()
        ));

        RequestReplyFuture<String, JwtRequest, JwtResponse> future =
                signInTemplate.sendAndReceive(record, Duration.ofSeconds(5));

        return future.get().value();
    }

}
