import event.rec.service.entities.CommonUserEntity;
import event.rec.service.entities.UserEntity;
import event.rec.service.listener.CommonUserListener;
import event.rec.service.repository.CommonUserRepository;
import event.rec.service.repository.UserRepository;
import event.rec.service.requests.CommonUserRegistrationRequest;
import event.rec.service.service.CommonUserService;
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
public class CommonUserListenerUnitTest {

    private CommonUserListener commonUserListener;

    @Mock
    private UserRepository userRepository;
    @Mock
    private CommonUserRepository commonUserRepository;

    private CommonUserRegistrationRequest request;

    @BeforeEach
    void setUp() {

        CommonUserService commonUserService = new CommonUserService(commonUserRepository);

        UserRoleIdentifier userRoleIdentifier = new UserRoleIdentifier(List.of(commonUserService));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        UserService userService = new UserService(userRepository, userRoleIdentifier, passwordEncoder);

        commonUserListener = new CommonUserListener(userService, commonUserService);

        request = new CommonUserRegistrationRequest();
        request.setFullName("Test User");
        request.setLogin("testUser");
        request.setPassword("testPassword");
        request.setPhoneNumber("testPhoneNumber");
        request.setUserType("USER");
    }

    @Test
    public void testRegisterExistingUser() {

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(new UserEntity()));

        assertEquals(false, commonUserListener.listenRegister(request));
    }

    @Test
    public void testRegisterNewUser() {

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(commonUserRepository.save(any(CommonUserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals(true, commonUserListener.listenRegister(request));
    }
}
