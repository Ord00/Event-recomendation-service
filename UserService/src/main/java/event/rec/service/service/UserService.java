package event.rec.service.service;

import event.rec.service.dto.UserDto;
import event.rec.service.entities.UserEntity;
import event.rec.service.repository.UserRepository;
import event.rec.service.utils.CustomUserDetails;
import event.rec.service.utils.UserRoleIdentifier;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static event.rec.service.mappers.UserMapper.userDTOToUserEntity;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleIdentifier userRoleIdentifier;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserEntity> findUserEntityByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(login));

        String role = userRoleIdentifier.determineUserRole(user.getId());

        return new CustomUserDetails(
                user.getLogin(),
                user.getPassword(),
                role
        );
    }

    @Transactional
    public UserEntity createNewUser(UserDto userDTO) {

        UserDto userWithPasswordDTO = new UserDto(
                userDTO.login(),
                passwordEncoder.encode(userDTO.password()));
        UserEntity userEntity = userDTOToUserEntity(userWithPasswordDTO);
        return userRepository.save(userEntity);
    }

    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
