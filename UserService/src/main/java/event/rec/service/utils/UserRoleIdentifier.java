package event.rec.service.utils;

import event.rec.service.interfaces.UserChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRoleIdentifier {

    private final List<UserChecker> userCheckers;

    public String determineUserRole(UUID userId) {

        return userCheckers.stream()
                .filter(checker -> checker.isUserInRole(userId))
                .findFirst()
                .map(UserChecker::getRoleName)
                .orElseThrow(() -> new IllegalStateException("User has no assigned role"));
    }
}
