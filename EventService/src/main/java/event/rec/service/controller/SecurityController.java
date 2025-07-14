package event.rec.service.controller;

import event.rec.service.JwtTokenUtils;
import event.rec.service.dto.UserDto;
import event.rec.service.enums.ErrorMessage;
import event.rec.service.exceptions.AppError;
import event.rec.service.requests.JwtRequest;
import event.rec.service.requests.RegistrationRequest;
import event.rec.service.responses.JwtResponse;
import event.rec.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SecurityController {

    private final UserService userService;
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

    @PostMapping("/register")
    public ResponseEntity<?> registration(@Validated @RequestBody RegistrationRequest registrationRequest) {

        if (userService.findUserEntityByLogin(registrationRequest.login()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
                    ErrorMessage.USER_EXISTS.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }

        UserDto userDTO = new UserDto(registrationRequest.login(), registrationRequest.password());
        userService.createNewUser(userDTO);

        return createAuthToken(new JwtRequest(registrationRequest.login(), registrationRequest.password()));
    }
}
