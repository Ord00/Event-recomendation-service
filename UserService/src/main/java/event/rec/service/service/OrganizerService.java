package event.rec.service.service;

import event.rec.service.dto.OrganizerDto;
import event.rec.service.entities.OrganizerEntity;
import event.rec.service.entities.UserEntity;
import event.rec.service.repository.OrganizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static event.rec.service.mappers.OrganizerMapper.OrganizerDTOToEntity;

@Service
@RequiredArgsConstructor
public class OrganizerService {

    private final OrganizerRepository organizerRepository;

    @Transactional
    public void createOrganizer(UserEntity userEntity, OrganizerDto organizerDTO) {
        OrganizerEntity organizerEntity = OrganizerDTOToEntity(organizerDTO);
        organizerEntity.setUser(userEntity);
        organizerRepository.save(organizerEntity);
    }
}