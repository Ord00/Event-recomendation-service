package event.rec.service.utils;

import event.rec.service.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class UserRoleIdentifier {

    private final ApplicationContext context;

    public String determineUserRole(Long userId) {
        return Arrays.stream(UserRole.values())
                .filter(role -> userExistsInRepository(role, userId))
                .findFirst()
                .map(UserRole::getRoleName)
                .orElseThrow(() -> new IllegalStateException("User has no assigned role"));
    }

    private boolean userExistsInRepository(UserRole role, Long userId) {
        JpaRepository<?, Long> repository = context.getBean(role.getRepositoryClass());
        return repository.existsById(userId);
    }
}
