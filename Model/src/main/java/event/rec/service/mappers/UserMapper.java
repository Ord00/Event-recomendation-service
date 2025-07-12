package event.rec.service.mappers;

import event.rec.service.dto.UserDetailsDto;
import event.rec.service.dto.UserDto;
import event.rec.service.entities.UserEntity;

public final class UserMapper {

    public static UserEntity UserDTOToUserEntity(UserDto userDTO) {
        return new UserEntity(userDTO.login(), userDTO.password());
    }

    public static UserDetailsDto toUserDetailsDto(UserEntity user) {
        return new UserDetailsDto(
                user.getId(),
                user.getLogin()
        );
    }
}