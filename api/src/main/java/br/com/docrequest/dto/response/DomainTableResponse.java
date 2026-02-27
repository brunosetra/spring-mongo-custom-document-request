package br.com.docrequest.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class DomainTableResponse {

    private String uuid;
    private String name;
    private String description;
    private String columnId;
    private List<String> columns;
    private List<Map<String, String>> rows;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
