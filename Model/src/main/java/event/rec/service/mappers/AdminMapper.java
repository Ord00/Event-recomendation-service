package event.rec.service.mappers;

import event.rec.service.dto.AdminDto;
import event.rec.service.entities.AdminEntity;

public class AdminMapper {
    public static AdminEntity AdminDTOToEntity(AdminDto adminDTO) {
        return new AdminEntity(adminDTO.fullName());
    }
}
