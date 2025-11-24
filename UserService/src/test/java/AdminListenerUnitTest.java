import event.rec.service.entities.AdminEntity;
import event.rec.service.entities.UserEntity;
import event.rec.service.listener.AdminListener;
import event.rec.service.repository.AdminRepository;
import event.rec.service.repository.UserRepository;
import event.rec.service.requests.AdminRegistrationRequest;
import event.rec.service.service.AdminService;
import event.rec.service.service.UserService;
import event.rec.service.utils.UserRoleIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminListenerUnitTest {

    private AdminListener adminListener;

    @Mock
    private UserRepository userRepository;
    @Mock
    private AdminRepository adminRepository;

    private AdminRegistrationRequest request;

    @BeforeEach
    void setUp() {

        AdminService adminService = new AdminService(adminRepository);

        UserRoleIdentifier userRoleIdentifier = new UserRoleIdentifier(List.of(adminService));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        UserService userService = new UserService(userRepository, userRoleIdentifier, passwordEncoder);

        adminListener = new AdminListener(userService, adminService);

        request = new AdminRegistrationRequest();
        request.setFullName("Test User");
        request.setLogin("testUser");
        request.setPassword("testPassword");
        request.setUserType("ADMIN");
    }

    @Test
    public void testRegisterExistingUser() {

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(new UserEntity()));

        assertEquals(false, adminListener.listenRegister(request));
    }

    @Test
    public void testRegisterNewUser() {

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(adminRepository.save(any(AdminEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals(true, adminListener.listenRegister(request));
    }
}
