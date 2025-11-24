import event.rec.service.enums.ErrorMessage;
import event.rec.service.exceptions.RegisterUserException;
import event.rec.service.requests.OrganizerRegistrationRequest;
import event.rec.service.service.OrganizerRegisterService;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrganizerRegisterServiceUnitTest {

    @Mock
    private ReplyingKafkaTemplate<String, OrganizerRegistrationRequest, Boolean> organizerRegisterTemplate;

    @InjectMocks
    private OrganizerRegisterService organizerRegisterService;

    private OrganizerRegistrationRequest request;
    private ConsumerRecord<String, Boolean> consumerRecord;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {

        String organizerRegisterRequestTopic = "organizer-register-request";
        ReflectionTestUtils.setField(organizerRegisterService,
                "registerOrganizerRequestTopic",
                organizerRegisterRequestTopic);
        String organizerRegisterResponseTopic = "organizer-register-response";
        ReflectionTestUtils.setField(organizerRegisterService,
                "registerOrganizerReplyTopic",
                organizerRegisterResponseTopic);

        request = new OrganizerRegistrationRequest();
        request.setOrganizerName("Test User");
        request.setLogin("testUser");
        request.setPassword("testPassword");
        request.setUserType("ORGANIZER");

        consumerRecord = mock(ConsumerRecord.class);
        RequestReplyFuture<String, OrganizerRegistrationRequest, Boolean> future = mock(RequestReplyFuture.class);

        doReturn(consumerRecord).when(future).get();
        doReturn(future).when(organizerRegisterTemplate).sendAndReceive(
                any(ProducerRecord.class),
                any(Duration.class));
    }

    @Test
    void testRegisterExistingUser() {

        when(consumerRecord.value()).thenReturn(Boolean.FALSE);

        RegisterUserException exception = assertThrows(
                RegisterUserException.class,
                () -> organizerRegisterService.registerUser(request)
        );

        assertEquals(ErrorMessage.USER_EXISTS.getMessage(), exception.getMessage());
    }

    @Test
    void testRegisterNewUser() {

        when(consumerRecord.value()).thenReturn(Boolean.TRUE);

        assertDoesNotThrow(() -> organizerRegisterService.registerUser(request));
    }
}
