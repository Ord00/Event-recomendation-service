package event.rec.service.utils;

import event.rec.service.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRoleIdentifier {

    private final ApplicationContext context;

    public String determineUserRole(UUID userId) {
        return Arrays.stream(UserRole.values())
                .filter(role -> userExistsInRepository(role, userId))
                .findFirst()
                .map(UserRole::getRoleName)
                .orElseThrow(() -> new IllegalStateException("User has no assigned role"));
    }

    private boolean userExistsInRepository(UserRole role, UUID userId) {
        JpaRepository<?, UUID> repository = context.getBean(role.getRepositoryClass());
        return repository.existsById(userId);
    }
}
