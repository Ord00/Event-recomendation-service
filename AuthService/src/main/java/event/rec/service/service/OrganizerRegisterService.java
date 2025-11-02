package event.rec.service.service;

import event.rec.service.enums.ErrorMessage;
import event.rec.service.exceptions.RegisterUserException;
import event.rec.service.interfaces.UserRegistrable;
import event.rec.service.requests.OrganizerRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static event.rec.service.utils.CommonUserRegistrar.registerUserInCommon;

@Service
@RequiredArgsConstructor
public class OrganizerRegisterService implements UserRegistrable<OrganizerRegistrationRequest> {

    private final ReplyingKafkaTemplate<String, OrganizerRegistrationRequest, Boolean> organizerRegisterTemplate;
    @Value("${kafka.register.organizer.request}")
    private String registerOrganizerRequestTopic;
    @Value("${kafka.register.organizer.response}")
    private String registerOrganizerReplyTopic;

    public void registerUser(OrganizerRegistrationRequest request) throws ExecutionException, InterruptedException {

        Boolean isRegistered = registerUserInCommon(
                registerOrganizerRequestTopic,
                registerOrganizerReplyTopic,
                request,
                organizerRegisterTemplate);

        if (!isRegistered) {
            throw new RegisterUserException(ErrorMessage.USER_EXISTS.getMessage());
        }
    }
}
