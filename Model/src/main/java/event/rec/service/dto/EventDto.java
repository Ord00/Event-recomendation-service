package event.rec.service.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record EventDto(String title,
                       String description,
                       OffsetDateTime startTime,
                       OffsetDateTime endTime,
                       Long venueId,
                       List<Long> categoryIds) {}
