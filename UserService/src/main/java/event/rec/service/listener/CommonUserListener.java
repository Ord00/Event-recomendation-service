package event.rec.service.listener;

import event.rec.service.dto.CommonUserDto;
import event.rec.service.interfaces.UserListenable;
import event.rec.service.requests.CommonUserRegistrationRequest;
import event.rec.service.service.CommonUserService;
import event.rec.service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import static event.rec.service.utils.UserRegistrar.registerUser;

@Component
@RequiredArgsConstructor
public class CommonUserListener implements UserListenable<CommonUserRegistrationRequest> {

    private final UserService userService;
    CommonUserService commonUserService;

    @KafkaListener(topics = "${kafka.topics.find.by.id.common.request}")
    @SendTo
        public Long findByUsername(@Payload String username) {
        return userService.findIdByLoginAndRole(username, "USER");
    }

    @Transactional
    @KafkaListener(topics = "${kafka.topics.register.common.request}")
    @SendTo
    public Boolean listenRegister(@Payload CommonUserRegistrationRequest request) {
        return registerUser(userService, request, (userEntity, req) ->
                commonUserService.createCommonUser(
                        userEntity,
                        new CommonUserDto(req.getFullName(), req.getPhoneNumber())
                )
        );
    }
}
