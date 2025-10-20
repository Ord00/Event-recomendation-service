package event.rec.service.listener;

import event.rec.service.entities.AdminEntity;
import event.rec.service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminListener {

    private final AdminService adminService;

    @KafkaListener(topics = "${kafka.topics.find.by.id.admin.request}")
    @SendTo("${kafka.topics.find.by.id.admin.response}")
    public AdminEntity findById(@Payload Long id) {
        return adminService.findById(id);
    }
}
