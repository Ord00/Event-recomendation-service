package event.rec.service.interfaces;

import java.util.UUID;

public interface UserChecker {
    boolean isUserInRole(UUID userId);
    String getRoleName();
}
