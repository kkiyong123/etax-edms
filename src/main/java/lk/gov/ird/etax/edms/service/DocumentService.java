package lk.gov.ird.etax.edms.service;
import io.minio.*;
import io.minio.http.Method;
import lk.gov.ird.etax.edms.entity.Document;
import lk.gov.ird.etax.edms.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
@Service
public class DocumentService {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private DocumentRepository documentRepository;
    @Value("${minio.bucket-name}")
    private String bucketName;
    @Value("${MINIO_ENDPOINT:https://s3.openshift-storage.svc.cluster.local:443}")
    private String minioEndpoint;
    public Document uploadDocument(String tin, String category, MultipartFile file) throws Exception {
        ensureBucketExists();
        String objectKey = tin + "/" + category + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        minioClient.putObject(PutObjectArgs.builder()
            .bucket(bucketName)
            .object(objectKey)
            .stream(file.getInputStream(), file.getSize(), -1)
            .contentType(file.getContentType())
            .build());
        Document doc = new Document();
        doc.setTin(tin);
        doc.setFileName(objectKey);
        doc.setOriginalName(file.getOriginalFilename());
        doc.setFileSize(file.getSize());
        doc.setContentType(file.getContentType());
        doc.setBucketName(bucketName);
        doc.setObjectKey(objectKey);
        doc.setCategory(category);
        doc.setStatus("VERIFIED");
        doc.setVerifiedAt(LocalDateTime.now());
        return documentRepository.save(doc);
    }
    public Document getDocument(Long id) {
        return documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found: " + id));
    }
    public String getPresignedUrl(Long docId) throws Exception {
        Document doc = getDocument(docId);
        return "/api/edms/documents/" + docId + "/download";
    }
    public byte[] downloadDocument(Long docId) throws Exception {
        Document doc = getDocument(docId);
        try (var stream = minioClient.getObject(GetObjectArgs.builder()
            .bucket(doc.getBucketName())
            .object(doc.getObjectKey())
            .build())) {
            return stream.readAllBytes();
        }
    }
    public List<Document> getDocumentsByTin(String tin) {
        return documentRepository.findByTin(tin);
    }
    public Document verifyDocument(Long id) {
        Document doc = getDocument(id);
        doc.setStatus("VERIFIED");
        doc.setVerifiedAt(LocalDateTime.now());
        return documentRepository.save(doc);
    }
    public void deleteDocument(Long id) throws Exception {
        Document doc = getDocument(id);
        minioClient.removeObject(RemoveObjectArgs.builder()
            .bucket(doc.getBucketName())
            .object(doc.getObjectKey())
            .build());
        documentRepository.delete(doc);
    }
    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
            .bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(bucketName).build());
        }
    }
}
