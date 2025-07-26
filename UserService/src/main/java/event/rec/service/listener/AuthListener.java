package event.rec.service.listener;

import event.rec.service.dto.AdminDto;
import event.rec.service.dto.CommonUserDto;
import event.rec.service.dto.OrganizerDto;
import event.rec.service.dto.UserDto;
import event.rec.service.entities.UserEntity;
import event.rec.service.enums.ErrorMessage;
import event.rec.service.requests.AdminRegistrationRequest;
import event.rec.service.requests.CommonUserRegistrationRequest;
import event.rec.service.requests.JwtRequest;
import event.rec.service.requests.OrganizerRegistrationRequest;
import event.rec.service.requests.RegistrationRequest;
import event.rec.service.responses.JwtResponse;
import event.rec.service.service.AdminService;
import event.rec.service.service.CommonUserService;
import event.rec.service.service.OrganizerService;
import event.rec.service.service.UserService;
import event.rec.service.utils.JwtTokenUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
@AllArgsConstructor
public class AuthListener {
    private final UserService userService;
    private final CommonUserService commonUserService;
    private final OrganizerService organizerService;
    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    @KafkaListener(topics = "${kafka.signin.request}")
    @SendTo("${kafka.signin.response}")
    public JwtResponse listenSignIn(JwtRequest request) {
        try {
            UserDetails userDetails = userService.loadUserByUsername(request.login());

            if (passwordEncoder.matches(request.password(), userDetails.getPassword())) {
                String token = jwtTokenUtils.generateToken(userDetails);
                return new JwtResponse(token);
            }
            throw new BadCredentialsException(ErrorMessage.INCORRECT_USER_DATA.getMessage());
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage());
        }
    }

    @Transactional
    @KafkaListener(topics = "${kafka.register.common.request}")
    @SendTo("${kafka.register.common.response}")
    public Boolean listenRegisterCommonUser(CommonUserRegistrationRequest request) {
        return registerUser(request, (userId, req) ->
                commonUserService.createCommonUser(
                        userId,
                        new CommonUserDto(req.getFullName(), req.getPhoneNumber()))
        );
    }

    @Transactional
    @KafkaListener(topics = "${kafka.register.organizer.request}")
    @SendTo("${kafka.register.organizer.response}")
    public Boolean listenRegisterOrganizer(OrganizerRegistrationRequest request) {
        return registerUser(request, (userId, req) ->
                organizerService.createOrganizer(
                        userId,
                        new OrganizerDto(req.getOrganizerName()))
        );
    }

    @Transactional
    @KafkaListener(topics = "${kafka.register.admin.request}")
    @SendTo("${kafka.register.admin.response}")
    public Boolean listenRegisterAdmin(AdminRegistrationRequest request) {
        return registerUser(request, (userId, req) ->
                adminService.createAdmin(
                        userId,
                        new AdminDto(req.getFullName()))
        );
    }

    @Transactional
    public  <T extends RegistrationRequest> Boolean registerUser(
            T request,
            BiConsumer<Long, T> userTypeCreator) {
        if (userService.findUserEntityByLogin(request.getLogin()).isPresent()) {
            return false;
        }

        UserDto userDTO = new UserDto(request.getLogin(), request.getPassword());
        UserEntity createdUser = userService.createNewUser(userDTO);

        userTypeCreator.accept(createdUser.getId(), request);

        return true;
    }
}
