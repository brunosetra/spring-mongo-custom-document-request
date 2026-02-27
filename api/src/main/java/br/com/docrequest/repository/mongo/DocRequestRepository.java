package br.com.docrequest.repository.mongo;

import br.com.docrequest.domain.document.DocRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocRequestRepository extends MongoRepository<DocRequest, String> {

    Optional<DocRequest> findByUuidAndPartId(String uuid, String partId);

    Page<DocRequest> findByPartId(String partId, Pageable pageable);

    Page<DocRequest> findByPartIdAndDocRequestMetadataName(String partId, String metadataName, Pageable pageable);

    boolean existsByUuidAndPartId(String uuid, String partId);
}
