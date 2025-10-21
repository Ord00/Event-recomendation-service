package event.rec.service.mappers;

import event.rec.service.dto.VenueDto;
import event.rec.service.entities.VenueEntity;

public class VenueMapper {

    public static VenueDto venueEntityToDto(VenueEntity venue) {

        return new VenueDto(
                venue.getAddress(),
                venue.getLocation());
    }
}
