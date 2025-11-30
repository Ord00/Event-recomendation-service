package event.rec.service.service;

import event.rec.service.dto.CommonUserDto;
import event.rec.service.entities.CommonUserEntity;
import event.rec.service.entities.UserEntity;
import event.rec.service.interfaces.UserChecker;
import event.rec.service.repository.CommonUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static event.rec.service.enums.UserRole.USER;
import static event.rec.service.mappers.CommonUserMapper.commonUserDTOToEntity;

@Service
@RequiredArgsConstructor
public class CommonUserService implements UserChecker {

    private final CommonUserRepository commonUserRepository;

    @Transactional
    public void createCommonUser(UserEntity userEntity, CommonUserDto commonUserDTO) {

        CommonUserEntity commonUserEntity = commonUserDTOToEntity(commonUserDTO);
        commonUserEntity.setUserEntity(userEntity);
        commonUserRepository.save(commonUserEntity);
    }

    public boolean isUserInRole(UUID userId) {
        return commonUserRepository.existsById(userId);
    }

    public String getRoleName() {
        return USER.name();
    }
}
