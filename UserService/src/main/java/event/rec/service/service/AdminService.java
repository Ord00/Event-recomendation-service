package event.rec.service.service;

import event.rec.service.dto.AdminDto;
import event.rec.service.entities.AdminEntity;
import event.rec.service.entities.UserEntity;
import event.rec.service.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static event.rec.service.mappers.AdminMapper.adminDTOToEntity;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    @Transactional
    public void createAdmin(UserEntity userEntity, AdminDto adminDTO) {
        AdminEntity adminEntity = adminDTOToEntity(adminDTO);
        adminEntity.setUserEntity(userEntity);
        adminRepository.save(adminEntity);
    }

    public AdminEntity findById(Long id) {
        return adminRepository.findById(id).orElse(null);
    }
}
