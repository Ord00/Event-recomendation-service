package event.rec.service.service;

import event.rec.service.dto.AdminDto;
import event.rec.service.entities.AdminEntity;
import event.rec.service.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static event.rec.service.mappers.AdminMapper.AdminDTOToEntity;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    @Transactional
    public void createAdmin(Long id, AdminDto adminDTO) {
        AdminEntity adminEntity = AdminDTOToEntity(adminDTO);
        adminEntity.setId(id);
        adminRepository.save(adminEntity);
    }
}
