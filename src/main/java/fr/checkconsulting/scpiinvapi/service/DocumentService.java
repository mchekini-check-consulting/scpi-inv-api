package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.request.UserDocumentDto;
import fr.checkconsulting.scpiinvapi.mapper.UserDocumentMapper;
import fr.checkconsulting.scpiinvapi.model.entity.UserDocument;
import fr.checkconsulting.scpiinvapi.repository.UserDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class DocumentService {

    private final UserDocumentRepository userDocumentRepository;
    private final UserDocumentMapper userDocumentMapper;

    public DocumentService(UserDocumentRepository userDocumentRepository,
                           UserDocumentMapper userDocumentMapper) {
        this.userDocumentRepository = userDocumentRepository;
        this.userDocumentMapper = userDocumentMapper;
    }

    public void updateStatus(UserDocumentDto dto) {
        log.info("Mise à jour du statut du document id={} vers {}", dto.getId(), dto.getStatus());

        UserDocument entity = userDocumentRepository.findById(dto.getId())
                .orElseThrow(() -> {
                    log.error("Document introuvable id={}", dto.getId());
                    return new IllegalArgumentException("Document introuvable : " + dto.getId());
                });

        applyStatusUpdate(entity, dto);

        userDocumentRepository.save(entity);
        log.debug("Document id={} sauvegardé avec statut={}", dto.getId(), dto.getStatus());
    }

    public List<UserDocumentDto> getDocumentsByUserEmail(String email) {
        log.info("Récupération des documents pour userEmail={}", email);

        List<UserDocument> documents = userDocumentRepository.findByUserEmail(email);
        log.debug("Nombre de documents trouvés pour {} : {}", email, documents.size());

        return userDocumentMapper.toDtoList(documents);
    }

    private void applyStatusUpdate(UserDocument entity, UserDocumentDto dto) {
        entity.setStatus(dto.getStatus());
        entity.setLastUpdatedAt(dto.getLastUpdatedAt());
    }
}
