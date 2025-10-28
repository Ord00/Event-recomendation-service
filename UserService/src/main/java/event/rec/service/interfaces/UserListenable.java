package event.rec.service.interfaces;

import event.rec.service.requests.RegistrationRequest;

public interface UserListenable<T extends RegistrationRequest> {

    UserHolder findById(Long userId);
    Boolean listenRegister(T request);
}
