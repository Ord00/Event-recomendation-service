package event.rec.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/secured")
public class UserController {
    @GetMapping("/user")
    public String userAccess(Principal principal) {
        return principal == null ? null : principal.getName();
    }
}
