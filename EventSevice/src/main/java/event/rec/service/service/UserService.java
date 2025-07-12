package event.rec.service.service;

import event.rec.service.dto.UserDto;
import event.rec.service.entities.UserEntity;
import event.rec.service.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

import static event.rec.service.mappers.UserMapper.UserDTOToUserEntity;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public Optional<UserEntity> findUserEntityByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(login));

        return new User(
                user.getLogin(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

    public void createNewUser(UserDto userDTO) {
        UserDto userWithPasswordDTO = new UserDto(
                passwordEncoder.encode(userDTO.login()),
                userDTO.password());
        UserEntity userEntity = UserDTOToUserEntity(userWithPasswordDTO);
        userRepository.save(userEntity);
    }
}
