import event.rec.service.entities.OrganizerEntity;
import event.rec.service.entities.UserEntity;
import event.rec.service.listener.OrganizerListener;
import event.rec.service.repository.OrganizerRepository;
import event.rec.service.repository.UserRepository;
import event.rec.service.requests.OrganizerRegistrationRequest;
import event.rec.service.service.OrganizerService;
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
public class OrganizerListenerUnitTest {

    private OrganizerListener organizerListener;

    @Mock
    private UserRepository userRepository;
    @Mock
    private OrganizerRepository organizerRepository;

    private OrganizerRegistrationRequest request;

    @BeforeEach
    void setUp() {

        OrganizerService organizerService = new OrganizerService(organizerRepository);

        UserRoleIdentifier userRoleIdentifier = new UserRoleIdentifier(List.of(organizerService));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        UserService userService = new UserService(userRepository, userRoleIdentifier, passwordEncoder);

        organizerListener = new OrganizerListener(userService, organizerService);

        request = new OrganizerRegistrationRequest();
        request.setOrganizerName("Test User");
        request.setLogin("testUser");
        request.setPassword("testPassword");
        request.setUserType("ORGANIZER");
    }

    @Test
    public void testRegisterExistingUser() {

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(new UserEntity()));

        assertEquals(false, organizerListener.listenRegister(request));
    }

    @Test
    public void testRegisterNewUser() {

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(organizerRepository.save(any(OrganizerEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals(true, organizerListener.listenRegister(request));
    }
}
