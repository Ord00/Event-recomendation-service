package event.rec.service.responses;

import java.time.Duration;

public record EventSubscriptionResponse(EventResponse event,
                                        String eventSubscriptionStatus,
                                        Duration notifyTime) {
}
