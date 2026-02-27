package br.com.docrequest.repository.jpa;

import br.com.docrequest.domain.entity.DocRequestMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocRequestMetadataRepository extends JpaRepository<DocRequestMetadata, String> {

    /**
     * Find the currently active (enabled) version of a template by name.
     */
    Optional<DocRequestMetadata> findByNameAndEnabledTrue(String name);

    /**
     * Find a specific version of a template.
     */
    Optional<DocRequestMetadata> findByNameAndVersion(String name, int version);

    /**
     * List all versions of a template ordered by version descending.
     */
    List<DocRequestMetadata> findByNameOrderByVersionDesc(String name);

    /**
     * List all active templates.
     */
    List<DocRequestMetadata> findByEnabledTrueOrderByNameAsc();

    /**
     * Check if any version of a template exists by name.
     */
    boolean existsByName(String name);

    /**
     * Get the maximum version number for a given template name.
     */
    @Query("SELECT MAX(m.version) FROM DocRequestMetadata m WHERE m.name = :name")
    Optional<Integer> findMaxVersionByName(String name);

    /**
     * Disable all versions of a template (used for logical deletion).
     */
    @Modifying
    @Query("UPDATE DocRequestMetadata m SET m.enabled = false WHERE m.name = :name")
    void disableAllVersionsByName(String name);

    /**
     * Disable the currently active version before creating a new one.
     */
    @Modifying
    @Query("UPDATE DocRequestMetadata m SET m.enabled = false WHERE m.name = :name AND m.enabled = true")
    void disableActiveVersionByName(String name);
}
