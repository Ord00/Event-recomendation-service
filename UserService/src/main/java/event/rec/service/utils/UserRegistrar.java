package event.rec.service.utils;

import event.rec.service.dto.UserDto;
import event.rec.service.entities.UserEntity;
import event.rec.service.requests.RegistrationRequest;
import event.rec.service.service.UserService;

import java.util.function.BiConsumer;

public class UserRegistrar {

    public static <T extends RegistrationRequest> Boolean registerUser(
            UserService userService,
            T request,
            BiConsumer<UserEntity, T> userTypeCreator) {
        if (userService.findUserEntityByLogin(request.getLogin()).isPresent()) {
            return false;
        }

        UserDto userDTO = new UserDto(request.getLogin(), request.getPassword());
        UserEntity createdUser = userService.createNewUser(userDTO);

        userTypeCreator.accept(createdUser, request);

        return true;
    }
}
