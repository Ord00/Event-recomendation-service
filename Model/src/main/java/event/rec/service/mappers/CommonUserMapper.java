package event.rec.service.mappers;

import event.rec.service.dto.CommonUserDto;
import event.rec.service.entities.CommonUserEntity;

public class CommonUserMapper {
    public static CommonUserEntity CommonUserDTOToEntity(CommonUserDto commonUserDTO) {
        return new CommonUserEntity(commonUserDTO.fullName(), commonUserDTO.phoneNumber());
    }
}
