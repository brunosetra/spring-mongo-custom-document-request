package br.com.docrequest.repository.mongo;

import br.com.docrequest.domain.document.DocRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface DocRequestRepository extends MongoRepository<DocRequest, String> {

    Optional<DocRequest> findByUuidAndPartId(String uuid, String partId);

    Page<DocRequest> findByPartId(String partId, Pageable pageable);

    Page<DocRequest> findByPartIdAndDocRequestMetadataName(String partId, String metadataName, Pageable pageable);

    boolean existsByUuidAndPartId(String uuid, String partId);

    /**
     * Check if a unique field value already exists for the given template and tenant.
     * This method uses MongoDB aggregation to search for documents containing the specified field value.
     */
    // boolean existsByPartIdAndDocRequestMetadataNameAndFieldsContainingValue(
    //     String partId, 
    //     String metadataName, 
    //     String fieldName, 
    //     Object value
    // );

    /**
     * Find existing document with specific field value.
     * This method uses MongoDB aggregation to search for documents containing the specified field value.
     */
    Optional<DocRequest> findByPartIdAndDocRequestMetadataNameAndFields(
        String partId, 
        String metadataName, 
        Map<String, Object> fields
    );
}
