package event.rec.service.controller;


import event.rec.service.dto.AdminDto;
import event.rec.service.dto.CommonUserDto;
import event.rec.service.dto.OrganizerDto;
import event.rec.service.entities.UserEntity;
import event.rec.service.requests.AdminRegistrationRequest;
import event.rec.service.requests.CommonUserRegistrationRequest;
import event.rec.service.requests.RegistrationRequest;
import event.rec.service.service.AdminService;
import event.rec.service.service.CommonUserService;
import event.rec.service.service.OrganizerService;
import event.rec.service.utils.JwtTokenUtils;
import event.rec.service.dto.UserDto;
import event.rec.service.enums.ErrorMessage;
import event.rec.service.exceptions.AppError;
import event.rec.service.requests.JwtRequest;
import event.rec.service.requests.OrganizerRegistrationRequest;
import event.rec.service.responses.JwtResponse;
import event.rec.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.BiConsumer;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AdminService adminService;
    private final OrganizerService organizerService;
    private final CommonUserService commonUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest jwtRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.login(),
                    jwtRequest.password()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(),
                    ErrorMessage.INCORRECT_USER_DATA.getMessage()), HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = userService.loadUserByUsername(jwtRequest.login());
        String token = jwtTokenUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/register/user")
    public ResponseEntity<?> registerCommonUser(@Validated @RequestBody CommonUserRegistrationRequest request) {
        return registerUser(request, (userId, req) ->
                commonUserService.createCommonUser(userId, new CommonUserDto(req.getFullName(), req.getPhoneNumber()))
        );
    }

    @PostMapping("/register/organizer")
    public ResponseEntity<?> registerOrganizer(@Validated @RequestBody OrganizerRegistrationRequest request) {
        return registerUser(request, (userId, req) ->
                organizerService.createOrganizer(userId, new OrganizerDto(req.getOrganizerName()))
        );
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerAdmin(@Validated @RequestBody AdminRegistrationRequest request) {
        return registerUser(request, (userId, req) ->
                adminService.createAdmin(userId, new AdminDto(req.getFullName()))
        );
    }

    private <T extends RegistrationRequest> ResponseEntity<?> registerUser(
            T request,
            BiConsumer<Long, T> userTypeCreator) {
        if (userService.findUserEntityByLogin(request.getLogin()).isPresent()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), ErrorMessage.USER_EXISTS.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }

        UserDto userDTO = new UserDto(request.getLogin(), request.getPassword());
        UserEntity createdUser = userService.createNewUser(userDTO);

        userTypeCreator.accept(createdUser.getId(), request);

        return createAuthToken(new JwtRequest(request.getLogin(), request.getPassword()));
    }
}
