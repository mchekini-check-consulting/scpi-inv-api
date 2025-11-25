package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.request.UserDocumentDto;
import fr.checkconsulting.scpiinvapi.mapper.UserDocumentMapper;
import fr.checkconsulting.scpiinvapi.model.entity.UserDocument;
import fr.checkconsulting.scpiinvapi.repository.UserDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class DocumentService {

    private final UserDocumentRepository userDocumentRepository;
    private final UserDocumentMapper userDocumentMapper;
    public DocumentService(UserDocumentRepository userDocumentRepository, UserDocumentMapper userDocumentMapper) {
        this.userDocumentRepository = userDocumentRepository;
        this.userDocumentMapper = userDocumentMapper;
    }

    public void updateStatus(UserDocumentDto dto) {

        UserDocument entity = userDocumentRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Document introuvable : " + dto.getId()));

        entity.setStatus(dto.getStatus());
        entity.setLastUpdatedAt(dto.getLastUpdatedAt());

        userDocumentRepository.save(entity);

        log.info("Statut mis à jour dans pour le document {} : {}", dto.getId(), dto.getStatus());
    }

    public List<UserDocumentDto> getDocumentsByUserEmail(String email) {
        return userDocumentMapper.toDtoList(
                userDocumentRepository.findByUserEmail(email)
        );
    }

}
