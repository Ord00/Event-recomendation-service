package event.rec.service.mappers;

import event.rec.service.dto.LocationDto;
import event.rec.service.dto.VenueDto;
import event.rec.service.entities.VenueEntity;
import org.locationtech.jts.geom.Point;

public class VenueMapper {

    public static VenueDto venueEntityToDto(VenueEntity venue) {

        return new VenueDto(
                venue.getAddress(),
                extractLocation(venue.getLocation()));
    }

    private static LocationDto extractLocation(Point point) {

        return point == null ? null : new LocationDto(point.getX(), point.getY());
    }
}
