package event.rec.service.service;

import event.rec.service.dto.AdminDto;
import event.rec.service.entities.AdminEntity;
import event.rec.service.entities.UserEntity;
import event.rec.service.interfaces.UserChecker;
import event.rec.service.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static event.rec.service.enums.UserRole.ADMIN;
import static event.rec.service.mappers.AdminMapper.adminDTOToEntity;

@Service
@RequiredArgsConstructor
public class AdminService implements UserChecker {

    private final AdminRepository adminRepository;

    @Transactional
    public void createAdmin(UserEntity userEntity, AdminDto adminDTO) {

        AdminEntity adminEntity = adminDTOToEntity(adminDTO);
        adminEntity.setUserEntity(userEntity);
        adminRepository.save(adminEntity);
    }

    public boolean isUserInRole(UUID userId) {
        return adminRepository.existsById(userId);
    }

    public String getRoleName() {
        return ADMIN.name();
    }
}
