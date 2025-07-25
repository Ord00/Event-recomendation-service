package event.rec.service.service;

import event.rec.service.dto.UserDto;
import event.rec.service.entities.UserEntity;
import event.rec.service.repository.AdminRepository;
import event.rec.service.repository.CommonUserRepository;
import event.rec.service.repository.OrganizerRepository;
import event.rec.service.repository.UserRepository;
import event.rec.service.utils.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static event.rec.service.mappers.UserMapper.UserDTOToUserEntity;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final OrganizerRepository organizerRepository;
    private final CommonUserRepository commonUserRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserEntity> findUserEntityByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(login));

        String role = determineUserRole(user.getId());

        return new CustomUserDetails(
                user.getLogin(),
                user.getPassword(),
                role
        );
    }

    private String determineUserRole(Long userId) {
        if (adminRepository.existsById(userId)) {
            return "ADMIN";
        } else if (organizerRepository.existsById(userId)) {
            return "ORGANIZER";
        } else if (commonUserRepository.existsById(userId)) {
            return "USER";
        }
        throw new IllegalStateException("User has no assigned role");
    }

    @Transactional
    public UserEntity createNewUser(UserDto userDTO) {
        UserDto userWithPasswordDTO = new UserDto(
                userDTO.login(),
                passwordEncoder.encode(userDTO.password()));
        UserEntity userEntity = UserDTOToUserEntity(userWithPasswordDTO);
        return userRepository.save(userEntity);
    }
}
