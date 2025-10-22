package event.rec.service.controller;

import event.rec.service.exceptions.RegisterUserException;
import event.rec.service.interfaces.UserRegistrable;
import event.rec.service.requests.AdminRegistrationRequest;
import event.rec.service.requests.CommonUserRegistrationRequest;
import event.rec.service.enums.ErrorMessage;
import event.rec.service.exceptions.AppError;
import event.rec.service.requests.JwtRequest;
import event.rec.service.requests.OrganizerRegistrationRequest;
import event.rec.service.requests.RegistrationRequest;
import event.rec.service.service.AdminRegisterService;
import event.rec.service.service.AuthService;
import event.rec.service.service.CommonUserRegisterService;
import event.rec.service.service.OrganizerRegisterService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final OrganizerRegisterService organizerRegisterService;
    private final CommonUserRegisterService commonRegisterService;
    private final AdminRegisterService adminRegisterService;

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody JwtRequest jwtRequest) {
        try {

            return ResponseEntity.ok(authService.signIn(jwtRequest));

        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/register/user")
    public ResponseEntity<?> registerCommonUser(@Validated @RequestBody CommonUserRegistrationRequest request) {
        return registerUser(request, commonRegisterService);
    }

    @PostMapping("/register/organizer")
    public ResponseEntity<?> registerOrganizer(@Validated @RequestBody OrganizerRegistrationRequest request) {
        return registerUser(request, organizerRegisterService);
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerAdmin(@Validated @RequestBody AdminRegistrationRequest request) {
        return registerUser(request, adminRegisterService);
    }

    private <T extends RegistrationRequest> ResponseEntity<?> registerUser(T request,
                                                                           UserRegistrable<T> registerService) {
        try {

            registerService.registerUser(request);
            return signIn(new JwtRequest(request.getLogin(), request.getPassword()));

        } catch (RegisterUserException e) {

            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), ErrorMessage.USER_EXISTS.getMessage()),
                    HttpStatus.BAD_REQUEST);

        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

