package event.rec.service.listener;

import event.rec.service.entities.CommonUserEntity;
import event.rec.service.service.CommonUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonUserListener {

    CommonUserService commonUserService;

    @KafkaListener(topics = "${kafka.topics.find.by.id.common.request}")
    @SendTo("${kafka.topics.find.by.id.common.response}")
    public CommonUserEntity findById(@Payload Long id) {
        return commonUserService.findById(id);
    }
}
