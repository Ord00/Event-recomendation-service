package event.rec.service.mappers;

import event.rec.service.dto.OrganizerDto;
import event.rec.service.entities.OrganizerEntity;

public class OrganizerMapper {

    public static OrganizerEntity organizerDTOToEntity(OrganizerDto organizerDTO) {
        return new OrganizerEntity(organizerDTO.organizerName());
    }

    public static OrganizerDto organizerEntityToDTO(OrganizerEntity organizerEntity) {
        return new OrganizerDto(organizerEntity.getOrganizerName());
    }
}
