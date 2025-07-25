package event.rec.service.service;

import event.rec.service.dto.OrganizerDto;
import event.rec.service.entities.OrganizerEntity;
import event.rec.service.repository.OrganizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static event.rec.service.mappers.OrganizerMapper.OrganizerDTOToEntity;

@Service
@RequiredArgsConstructor
public class OrganizerService {

    private final OrganizerRepository organizerRepository;

    public void createOrganizer(Long id, OrganizerDto organizerDTO) {
        OrganizerEntity organizerEntity = OrganizerDTOToEntity(organizerDTO);
        organizerEntity.setId(id);
        organizerRepository.save(organizerEntity);
    }
}