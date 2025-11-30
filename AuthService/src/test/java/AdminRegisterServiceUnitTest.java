import event.rec.service.enums.ErrorMessage;
import event.rec.service.exceptions.RegisterUserException;
import event.rec.service.requests.AdminRegistrationRequest;
import event.rec.service.service.AdminRegisterService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
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
public class AdminRegisterServiceUnitTest {

    @Mock
    private ReplyingKafkaTemplate<String, AdminRegistrationRequest, Boolean> adminRegisterTemplate;

    @InjectMocks
    private AdminRegisterService adminRegisterService;

    private AdminRegistrationRequest request;
    private ConsumerRecord<String, Boolean> consumerRecord;

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {

        String adminRegisterRequestTopic = "admin-register-request";
        ReflectionTestUtils.setField(adminRegisterService,
                "registerAdminRequestTopic",
                adminRegisterRequestTopic);
        String adminRegisterResponseTopic = "admin-register-response";
        ReflectionTestUtils.setField(adminRegisterService,
                "registerAdminReplyTopic",
                adminRegisterResponseTopic);

        request = new AdminRegistrationRequest();
        request.setFullName("Test User");
        request.setLogin("testUser");
        request.setPassword("testPassword");
        request.setUserType("ADMIN");

        consumerRecord = mock(ConsumerRecord.class);
        RequestReplyFuture<String, AdminRegistrationRequest, Boolean> future = mock(RequestReplyFuture.class);

        doReturn(consumerRecord).when(future).get();
        doReturn(future).when(adminRegisterTemplate).sendAndReceive(any(ProducerRecord.class), any(Duration.class));
    }

    @Test
    void testRegisterExistingUser() {

        when(consumerRecord.value()).thenReturn(Boolean.FALSE);

        RegisterUserException exception = assertThrows(
                RegisterUserException.class,
                () -> adminRegisterService.registerUser(request)
        );

        assertEquals(ErrorMessage.USER_EXISTS.getMessage(), exception.getMessage());
    }

    @Test
    void testRegisterNewUser() {

        when(consumerRecord.value()).thenReturn(Boolean.TRUE);

        assertDoesNotThrow(() -> adminRegisterService.registerUser(request));
    }
}
