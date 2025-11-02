package event.rec.service.service;

import event.rec.service.enums.ErrorMessage;
import event.rec.service.exceptions.RegisterUserException;
import event.rec.service.interfaces.UserRegistrable;
import event.rec.service.requests.AdminRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static event.rec.service.utils.CommonUserRegistrar.registerUserInCommon;

@Service
@RequiredArgsConstructor
public class AdminRegisterService implements UserRegistrable<AdminRegistrationRequest> {

    private final ReplyingKafkaTemplate<String, AdminRegistrationRequest, Boolean> adminRegisterTemplate;
    @Value("${kafka.register.admin.request}")
    private String registerAdminRequestTopic;
    @Value("${kafka.register.admin.response}")
    private String registerAdminReplyTopic;

    public void registerUser(AdminRegistrationRequest request) throws ExecutionException, InterruptedException {

        Boolean isRegistered = registerUserInCommon(
                registerAdminRequestTopic,
                registerAdminReplyTopic,
                request,
                adminRegisterTemplate);

        if (!isRegistered) {
            throw new RegisterUserException(ErrorMessage.USER_EXISTS.getMessage());
        }
    }

}
