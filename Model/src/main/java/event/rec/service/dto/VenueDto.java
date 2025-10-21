package event.rec.service.dto;

import org.locationtech.jts.geom.Point;

public record VenueDto(String address,
                       Point location) {
}
