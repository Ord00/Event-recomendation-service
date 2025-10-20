package event.rec.service.listener;

import event.rec.service.entities.OrganizerEntity;
import event.rec.service.service.OrganizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizerListener {

    private final OrganizerService organizerService;

    @KafkaListener(topics = "${kafka.topics.find.by.id.organizer.request}")
    @SendTo("${kafka.topics.find.by.id.organizer.response}")
    public OrganizerEntity findById(@Payload Long id) {
        return organizerService.findById(id);
    }
}
