package event.rec.service.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record EventDto(String title,
                       String description,
                       OffsetDateTime startTime,
                       OffsetDateTime endTime,
                       String recurrence,
                       String status,
                       Long organizerId,
                       Long venueId,
                       List<Long> categoryIds) {}
