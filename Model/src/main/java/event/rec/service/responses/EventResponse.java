package event.rec.service.responses;

import event.rec.service.dto.CategoryDto;
import event.rec.service.dto.OrganizerDto;
import event.rec.service.dto.VenueDto;

import java.time.OffsetDateTime;
import java.util.List;

public record EventResponse(Long id,
                            String title,
                            String description,
                            OffsetDateTime startTime,
                            OffsetDateTime endTime,
                            String recurrence,
                            String eventStatus,
                            OrganizerDto organizer,
                            VenueDto venue,
                            List<CategoryDto> categories) {
}
