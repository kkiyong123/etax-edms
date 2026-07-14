package lk.gov.ird.etax.edms.repository;

import lk.gov.ird.etax.edms.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByTin(String tin);
    List<Document> findByTinAndCategory(String tin, String category);
    List<Document> findByStatus(String status);
}
