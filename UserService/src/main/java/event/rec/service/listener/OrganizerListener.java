package event.rec.service.listener;

import event.rec.service.dto.OrganizerDto;
import event.rec.service.entities.OrganizerEntity;
import event.rec.service.interfaces.UserListenable;
import event.rec.service.requests.OrganizerRegistrationRequest;
import event.rec.service.service.OrganizerService;
import event.rec.service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizerListener implements UserListenable<OrganizerRegistrationRequest> {

    private final UserService userService;
    private final OrganizerService organizerService;

    @KafkaListener(topics = "${kafka.topics.find.by.id.organizer.request}")
    @SendTo
    public OrganizerEntity findById(@Payload Long id) {
        return organizerService.findById(id);
    }

    @Transactional
    @KafkaListener(topics = "${kafka.topics.register.organizer.request}")
    @SendTo
    public Boolean listenRegister(@Payload OrganizerRegistrationRequest request) {
        return registerUser(userService, request, (userEntity, req) ->
                organizerService.createOrganizer(
                        userEntity,
                        new OrganizerDto(req.getOrganizerName())
                )
        );
    }
}
