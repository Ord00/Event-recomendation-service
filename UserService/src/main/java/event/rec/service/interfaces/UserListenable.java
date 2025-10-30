package event.rec.service.interfaces;

import event.rec.service.requests.RegistrationRequest;

public interface UserListenable<T extends RegistrationRequest> {

    Long findByUsername(String username);
    Boolean listenRegister(T request);
}
