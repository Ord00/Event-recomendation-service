import event.rec.service.enums.ErrorMessage;
import event.rec.service.exceptions.RegisterUserException;
import event.rec.service.requests.CommonUserRegistrationRequest;
import event.rec.service.service.CommonUserRegisterService;
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
public class CommonUserRegisterServiceUnitTest {

    @Mock
    private ReplyingKafkaTemplate<String, CommonUserRegistrationRequest, Boolean> commonUserRegisterTemplate;

    @InjectMocks
    private CommonUserRegisterService commonUserRegisterService;

    private CommonUserRegistrationRequest request;
    private ConsumerRecord<String, Boolean> consumerRecord;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {

        String commonUserRegisterRequestTopic = "common-user-register-request";
        ReflectionTestUtils.setField(commonUserRegisterService,
                "registerCommonUserRequestTopic",
                commonUserRegisterRequestTopic);
        String commonUserRegisterResponseTopic = "common-user-register-response";
        ReflectionTestUtils.setField(commonUserRegisterService,
                "registerCommonUserReplyTopic",
                commonUserRegisterResponseTopic);

        request = new CommonUserRegistrationRequest();
        request.setFullName("Test User");
        request.setLogin("testUser");
        request.setPassword("testPassword");
        request.setPhoneNumber("testPhoneNumber");
        request.setUserType("USER");

        consumerRecord = mock(ConsumerRecord.class);
        RequestReplyFuture<String, CommonUserRegistrationRequest, Boolean> future = mock(RequestReplyFuture.class);

        doReturn(consumerRecord).when(future).get();
        doReturn(future).when(commonUserRegisterTemplate).sendAndReceive(
                any(ProducerRecord.class),
                any(Duration.class));
    }

    @Test
    void testRegisterExistingUser() {

        when(consumerRecord.value()).thenReturn(Boolean.FALSE);

        RegisterUserException exception = assertThrows(
                RegisterUserException.class,
                () -> commonUserRegisterService.registerUser(request)
        );

        assertEquals(ErrorMessage.USER_EXISTS.getMessage(), exception.getMessage());
    }

    @Test
    void testRegisterNewUser() {

        when(consumerRecord.value()).thenReturn(Boolean.TRUE);

        assertDoesNotThrow(() -> commonUserRegisterService.registerUser(request));
    }
}
