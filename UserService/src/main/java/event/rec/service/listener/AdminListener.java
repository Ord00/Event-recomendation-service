package event.rec.service.listener;

import event.rec.service.dto.AdminDto;
import event.rec.service.entities.AdminEntity;
import event.rec.service.interfaces.UserListenable;
import event.rec.service.requests.AdminRegistrationRequest;
import event.rec.service.service.AdminService;
import event.rec.service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminListener implements UserListenable<AdminRegistrationRequest> {

    private final UserService userService;
    private final AdminService adminService;

    @KafkaListener(topics = "${kafka.topics.find.by.id.admin.request}")
    @SendTo
    public AdminEntity findById(@Payload Long id) {
        return adminService.findById(id);
    }

    @Transactional
    @KafkaListener(topics = "${kafka.topics.register.admin.request}")
    @SendTo
    public Boolean listenRegister(@Payload AdminRegistrationRequest request) {
        return registerUser(userService, request, (userEntity, req) ->
                adminService.createAdmin(
                        userEntity,
                        new AdminDto(req.getFullName())
                )
        );
    }
}
