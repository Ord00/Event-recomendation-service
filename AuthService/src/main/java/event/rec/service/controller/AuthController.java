package event.rec.service.controller;

import event.rec.service.requests.AdminRegistrationRequest;
import event.rec.service.requests.CommonUserRegistrationRequest;
import event.rec.service.requests.RegistrationRequest;
import event.rec.service.enums.ErrorMessage;
import event.rec.service.exceptions.AppError;
import event.rec.service.requests.JwtRequest;
import event.rec.service.requests.OrganizerRegistrationRequest;
import event.rec.service.responses.JwtResponse;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ReplyingKafkaTemplate<String, JwtRequest, JwtResponse> signInTemplate;
    @Value("${kafka.signin.request}")
    private String signInRequestTopic;
    @Value("${kafka.signin.response}")
    private String signInReplyTopic;

    private final ReplyingKafkaTemplate<String, CommonUserRegistrationRequest, Boolean> commonRegisterTemplate;
    @Value("${kafka.register.common.request}")
    private String registerCommonUserRequestTopic;
    @Value("${kafka.register.common.response}")
    private String registerCommonUserReplyTopic;

    private final ReplyingKafkaTemplate<String, OrganizerRegistrationRequest, Boolean> organizerRegisterTemplate;
    @Value("${kafka.register.organizer.request}")
    private String registerOrganizerRequestTopic;
    @Value("${kafka.register.organizer.response}")
    private String registerOrganizerReplyTopic;

    private final ReplyingKafkaTemplate<String, AdminRegistrationRequest, Boolean> adminRegisterTemplate;
    @Value("${kafka.register.admin.request}")
    private String registerAdminRequestTopic;
    @Value("${kafka.register.admin.response}")
    private String registerAdminReplyTopic;

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody JwtRequest jwtRequest) {
        try {
            ProducerRecord<String, JwtRequest> record = new ProducerRecord<>(
                    signInRequestTopic,
                    jwtRequest
            );

            record.headers().add(new RecordHeader(
                    KafkaHeaders.REPLY_TOPIC,
                    signInReplyTopic.getBytes()
            ));

            RequestReplyFuture<String, JwtRequest, JwtResponse> future =
                    signInTemplate.sendAndReceive(record, Duration.ofSeconds(5));

            return ResponseEntity.ok(future.get().value());

        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/register/user")
    public ResponseEntity<?> registerCommonUser(@Validated @RequestBody CommonUserRegistrationRequest request) {
        return registerUser(
                registerCommonUserRequestTopic,
                registerCommonUserReplyTopic,
                request,
                commonRegisterTemplate);
    }

    @PostMapping("/register/organizer")
    public ResponseEntity<?> registerOrganizer(@Validated @RequestBody OrganizerRegistrationRequest request) {
        return registerUser(
                registerOrganizerRequestTopic,
                registerOrganizerReplyTopic,
                request,
                organizerRegisterTemplate);
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerAdmin(@Validated @RequestBody AdminRegistrationRequest request) {
        return registerUser(
                registerAdminRequestTopic,
                registerAdminReplyTopic,
                request,
                adminRegisterTemplate);
    }

    private <T extends RegistrationRequest> ResponseEntity<?> registerUser(String requestTopic,
                                           String responseTopic,
                                           T request,
                                           ReplyingKafkaTemplate<String, T, Boolean> registerTemplate) {
        try {
            ProducerRecord<String, T> record =
                    new ProducerRecord<>(requestTopic, request);

            record.headers().add(new RecordHeader(
                    KafkaHeaders.REPLY_TOPIC,
                    responseTopic.getBytes()
            ));

            RequestReplyFuture<String, T, Boolean> future =
                    registerTemplate.sendAndReceive(record, Duration.ofSeconds(5));

            if (future.get().value()) {
                return signIn(new JwtRequest(request.getLogin(), request.getPassword()));
            } else {
                return new ResponseEntity<>(
                        new AppError(HttpStatus.BAD_REQUEST.value(), ErrorMessage.USER_EXISTS.getMessage()),
                        HttpStatus.BAD_REQUEST);
            }
        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
