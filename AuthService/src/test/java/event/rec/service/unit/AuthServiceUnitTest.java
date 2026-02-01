package event.rec.service.unit;

import event.rec.service.requests.JwtRequest;
import event.rec.service.responses.JwtResponse;
import event.rec.service.service.AuthService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceUnitTest {

    @Mock
    private ReplyingKafkaTemplate<String, JwtRequest, JwtResponse> signInTemplate;

    @InjectMocks
    private AuthService authService;

    private JwtRequest request;
    private ConsumerRecord<String, JwtResponse> consumerRecord;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {

        String signInRequestTopic = "sign-in-request";
        ReflectionTestUtils.setField(authService,
                "signInRequestTopic",
                signInRequestTopic);
        String signInResponseTopic = "sign-in-response";
        ReflectionTestUtils.setField(authService,
                "signInReplyTopic",
                signInResponseTopic);

        request = new JwtRequest("testUser", "testPassword");

        consumerRecord = mock(ConsumerRecord.class);
        RequestReplyFuture<String, JwtRequest, JwtResponse> future = mock(RequestReplyFuture.class);

        doReturn(consumerRecord).when(future).get();
        doReturn(future).when(signInTemplate).sendAndReceive(any(ProducerRecord.class), any(Duration.class));
    }

    @Test
    void testSignIn() throws ExecutionException, InterruptedException {

        JwtResponse response = new JwtResponse("testToken", null);

        when(consumerRecord.value()).thenReturn(response);

        assertEquals(authService.signIn(request), response);
    }
}
