package event.rec.service.service;

import event.rec.service.enums.ErrorMessage;
import event.rec.service.exceptions.RegisterUserException;
import event.rec.service.interfaces.UserRegistrable;
import event.rec.service.requests.CommonUserRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class CommonUserRegisterService implements UserRegistrable<CommonUserRegistrationRequest> {

    private final ReplyingKafkaTemplate<String, CommonUserRegistrationRequest, Boolean> commonRegisterTemplate;
    @Value("${kafka.register.common.request}")
    private String registerCommonUserRequestTopic;
    @Value("${kafka.register.common.response}")
    private String registerCommonUserReplyTopic;

    public void registerUser(CommonUserRegistrationRequest request) throws ExecutionException, InterruptedException {

        Boolean isRegistered = registerUserInCommon(
                registerCommonUserRequestTopic,
                registerCommonUserReplyTopic,
                request,
                commonRegisterTemplate);

        if (!isRegistered) {
            throw new RegisterUserException(ErrorMessage.USER_EXISTS.getMessage());
        }
    }
}
