package br.com.docrequest.mapper;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.dto.request.DocRequestFieldMetadataRequest;
import br.com.docrequest.dto.request.DocRequestMetadataCreateRequest;
import br.com.docrequest.dto.response.DocRequestMetadataResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DocRequestMetadataMapper {

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "version", constant = "1")
    @Mapping(target = "fields", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DocRequestMetadata toEntity(DocRequestMetadataCreateRequest request);

    @Mapping(target = "uuid", ignore = true)
@Mapping(target = "docRequestMetadata", ignore = true)
@Mapping(target = "unique", source = "unique")
DocRequestFieldMetadata toFieldEntity(DocRequestFieldMetadataRequest request);

    DocRequestMetadataResponse toResponse(DocRequestMetadata metadata);

    @Mapping(target = "fieldOrder", source = "fieldOrder")
    @Mapping(target = "unique", source = "unique")
    DocRequestMetadataResponse.FieldMetadataResponse toFieldResponse(DocRequestFieldMetadata field);

    List<DocRequestMetadataResponse.FieldMetadataResponse> toFieldResponseList(List<DocRequestFieldMetadata> fields);
}
