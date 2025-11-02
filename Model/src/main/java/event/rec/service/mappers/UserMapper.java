package event.rec.service.mappers;

import event.rec.service.dto.UserDto;
import event.rec.service.entities.UserEntity;

public final class UserMapper {

    public static UserEntity userDTOToUserEntity(UserDto userDTO) {
        return new UserEntity(userDTO.login(), userDTO.password());
    }
}