package event.rec.service.service;

import event.rec.service.dto.CommonUserDto;
import event.rec.service.entities.CommonUserEntity;
import event.rec.service.repository.CommonUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static event.rec.service.mappers.CommonUserMapper.CommonUserDTOToEntity;

@Service
@RequiredArgsConstructor
public class CommonUserService {

    private final CommonUserRepository commonUserRepository;

    public void createCommonUser(Long id, CommonUserDto commonUserDTO) {
        CommonUserEntity commonUserEntity = CommonUserDTOToEntity(commonUserDTO);
        commonUserEntity.setId(id);
        commonUserRepository.save(commonUserEntity);
    }
}
