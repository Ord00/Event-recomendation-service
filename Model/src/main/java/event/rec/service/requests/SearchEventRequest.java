package event.rec.service.requests;

import java.time.OffsetDateTime;
import java.util.List;

public record SearchEventRequest(List<Long> categoryIds,
                                 OffsetDateTime from,
                                 OffsetDateTime to) {}
