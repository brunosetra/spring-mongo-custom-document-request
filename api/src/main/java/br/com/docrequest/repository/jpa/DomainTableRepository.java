package br.com.docrequest.repository.jpa;

import br.com.docrequest.domain.entity.DomainTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomainTableRepository extends JpaRepository<DomainTable, String> {

    Optional<DomainTable> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT dt FROM DomainTable dt LEFT JOIN FETCH dt.rows WHERE dt.name = :name")
    Optional<DomainTable> findByNameWithRows(String name);

    List<DomainTable> findAllByOrderByNameAsc();
}
