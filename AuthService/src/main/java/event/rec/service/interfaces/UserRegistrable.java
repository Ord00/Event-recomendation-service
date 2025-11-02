package event.rec.service.interfaces;

import event.rec.service.requests.RegistrationRequest;

import java.util.concurrent.ExecutionException;

public interface UserRegistrable<T extends RegistrationRequest> {

    void registerUser(T request) throws ExecutionException, InterruptedException;
}
