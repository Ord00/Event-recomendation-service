package event.rec.service.mappers;

import event.rec.service.dto.OrganizerDto;
import event.rec.service.entities.OrganizerEntity;

public class OrganizerMapper {
    public static OrganizerEntity OrganizerDTOToEntity(OrganizerDto organizerDTO) {
        return new OrganizerEntity(organizerDTO.organizerName());
    }
}
