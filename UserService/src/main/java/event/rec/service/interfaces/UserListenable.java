package event.rec.service.interfaces;

import event.rec.service.requests.RegistrationRequest;

public interface UserListenable<T extends RegistrationRequest> {

    Boolean listenRegister(T request);
}
