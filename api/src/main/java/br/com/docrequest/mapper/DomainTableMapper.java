package br.com.docrequest.mapper;

import br.com.docrequest.domain.entity.DomainTable;
import br.com.docrequest.domain.entity.DomainTableRow;
import br.com.docrequest.dto.request.DomainTableCreateRequest;
import br.com.docrequest.dto.response.DomainTableResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DomainTableMapper {

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "rows", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DomainTable toEntity(DomainTableCreateRequest request);

    @Mapping(target = "rows", expression = "java(mapRows(domainTable))")
    DomainTableResponse toResponse(DomainTable domainTable);

    default List<Map<String, String>> mapRows(DomainTable domainTable) {
        if (domainTable.getRows() == null) {
            return List.of();
        }
        return domainTable.getRows().stream()
            .map(DomainTableRow::getValues)
            .collect(Collectors.toList());
    }
}
