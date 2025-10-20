package event.rec.service.service;

import event.rec.service.dto.CommonUserDto;
import event.rec.service.entities.CommonUserEntity;
import event.rec.service.entities.UserEntity;
import event.rec.service.repository.CommonUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static event.rec.service.mappers.CommonUserMapper.CommonUserDTOToEntity;

@Service
@RequiredArgsConstructor
public class CommonUserService {

    private final CommonUserRepository commonUserRepository;

    @Transactional
    public void createCommonUser(UserEntity userEntity, CommonUserDto commonUserDTO) {
        CommonUserEntity commonUserEntity = CommonUserDTOToEntity(commonUserDTO);
        commonUserEntity.setUserEntity(userEntity);
        commonUserRepository.save(commonUserEntity);
    }

    public CommonUserEntity findById(Long id) {
        return commonUserRepository.findById(id).orElse(null);
    }
}
