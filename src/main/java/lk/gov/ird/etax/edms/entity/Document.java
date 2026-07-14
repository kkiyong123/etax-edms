package lk.gov.ird.etax.edms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tin;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "bucket_name")
    private String bucketName;

    @Column(name = "object_key")
    private String objectKey;

    @Column(name = "category")
    private String category;

    @Column(name = "status")
    private String status;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }
}
