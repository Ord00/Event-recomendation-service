package event.rec.service.listener;

import event.rec.service.entities.UserEntity;
import event.rec.service.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetInfoListener {

    private UserService userService;

    @KafkaListener(topics = "${kafka.topics.find.by.id.request}")
    @SendTo("${kafka.topics.find.by.id.response}")
    public UserEntity findById(@Payload Long id) {
        return userService.findById(id);
    }
}
