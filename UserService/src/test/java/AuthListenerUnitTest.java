import event.rec.service.entities.UserEntity;
import event.rec.service.listener.AuthListener;
import event.rec.service.repository.AdminRepository;
import event.rec.service.repository.CommonUserRepository;
import event.rec.service.repository.OrganizerRepository;
import event.rec.service.repository.UserRepository;
import event.rec.service.requests.JwtRequest;
import event.rec.service.responses.JwtResponse;
import event.rec.service.service.AdminService;
import event.rec.service.service.CommonUserService;
import event.rec.service.service.OrganizerService;
import event.rec.service.service.UserService;
import event.rec.service.utils.TokenGenerator;
import event.rec.service.utils.UserRoleIdentifier;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthListenerUnitTest {

    private AuthListener authListener;

    @Mock
    private UserRepository userRepository;
    @Mock
    private CommonUserRepository commonUserRepository;
    @Mock
    private OrganizerRepository organizerRepository;
    @Mock
    private AdminRepository adminRepository;

    private UUID commonUserId;
    private UUID organizerId;
    private UUID adminId;

    TokenGenerator tokenGenerator;
    private String secret;
    private Duration lifetime;

    private String login;
    private String password;
    private PasswordEncoder passwordEncoder;
    private JwtRequest request;

    @BeforeEach
    void setUp() {

        CommonUserService commonUserService = new CommonUserService(commonUserRepository);
        OrganizerService organizerService = new OrganizerService(organizerRepository);
        AdminService adminService = new AdminService(adminRepository);

        UserRoleIdentifier userRoleIdentifier = new UserRoleIdentifier(
                List.of(commonUserService,
                        organizerService,
                        adminService));
        passwordEncoder = new BCryptPasswordEncoder();

        tokenGenerator = new TokenGenerator();
        secret = "a".repeat(32);
        lifetime = Duration.ofMillis(60000);
        ReflectionTestUtils.setField(tokenGenerator, "secret", secret);
        ReflectionTestUtils.setField(tokenGenerator, "lifetime",  lifetime);

        UserService userService = new UserService(userRepository, userRoleIdentifier, passwordEncoder);

        authListener = new AuthListener(userService, passwordEncoder, tokenGenerator);

        login = "testUser";
        password = "testPassword";
        request = new JwtRequest(login, password);

        commonUserId = UUID.randomUUID();
        organizerId = UUID.randomUUID();
        adminId = UUID.randomUUID();
    }

    @Test
    public void testSignInExistingCommonUser() {

        UserEntity user = new UserEntity(login, passwordEncoder.encode(password));
        user.setId(commonUserId);

        when(commonUserRepository.existsById(nullable(UUID.class))).thenAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            return id != null && id.equals(commonUserId);
        });
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(user));

        JwtResponse response = authListener.listenSignIn(request);

        assertNotNull(response.token());
        assertFalse(response.token().isEmpty());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(response.token())
                .getBody();

        assertEquals(commonUserId.toString(), claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());

        long tokenLifetime = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
        assertEquals(lifetime.toMillis(), tokenLifetime);
    }

    @Test
    public void testSignInExistingOrganizer() {

        UserEntity user = new UserEntity(login, passwordEncoder.encode(password));
        user.setId(organizerId);

        when(organizerRepository.existsById(nullable(UUID.class))).thenAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            return id != null && id.equals(organizerId);
        });
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(user));

        JwtResponse response = authListener.listenSignIn(request);

        assertNotNull(response.token());
        assertFalse(response.token().isEmpty());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(response.token())
                .getBody();

        assertEquals(organizerId.toString(), claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());

        long tokenLifetime = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
        assertEquals(lifetime.toMillis(), tokenLifetime);
    }

    @Test
    public void testSignInExistingAdmin() {

        UserEntity user = new UserEntity(login, passwordEncoder.encode(password));
        user.setId(adminId);

        when(adminRepository.existsById(nullable(UUID.class))).thenAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            return id != null && id.equals(adminId);
        });
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(user));

        JwtResponse response = authListener.listenSignIn(request);

        assertNotNull(response.token());
        assertFalse(response.token().isEmpty());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(response.token())
                .getBody();

        assertEquals(adminId.toString(), claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());

        long tokenLifetime = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
        assertEquals(lifetime.toMillis(), tokenLifetime);
    }
}
