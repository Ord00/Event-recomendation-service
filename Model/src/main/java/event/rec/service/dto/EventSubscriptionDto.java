package event.rec.service.dto;

import java.time.Duration;

public record EventSubscriptionDto(String username,
                                   Long eventId,
                                   Duration notifyTime) {
}
