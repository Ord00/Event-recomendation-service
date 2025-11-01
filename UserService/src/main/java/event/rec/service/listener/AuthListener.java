package event.rec.service.listener;

import event.rec.service.enums.ErrorMessage;
import event.rec.service.requests.JwtRequest;
import event.rec.service.responses.JwtResponse;
import event.rec.service.service.UserService;
import event.rec.service.utils.TokenGenerator;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthListener {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    @KafkaListener(topics = "${kafka.topics.sign.in.request}")
    @SendTo
    public JwtResponse listenSignIn(@Payload JwtRequest request) {
        try {
            UserDetails userDetails = userService.loadUserByUsername(request.login());

            if (passwordEncoder.matches(request.password(), userDetails.getPassword())) {
                String token = tokenGenerator.generateToken(userDetails);
                return new JwtResponse(token);
            }
            throw new BadCredentialsException(ErrorMessage.INCORRECT_USER_DATA.getMessage());
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage());
        }
    }
}
