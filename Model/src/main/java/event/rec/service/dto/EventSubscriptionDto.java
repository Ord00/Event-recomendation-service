package event.rec.service.dto;

import java.time.Duration;

public record EventSubscriptionDto(Long userId,
                                   Long eventId,
                                   Duration notifyTime) {
}
