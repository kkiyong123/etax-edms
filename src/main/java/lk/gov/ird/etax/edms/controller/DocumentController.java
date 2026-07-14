package lk.gov.ird.etax.edms.controller;
import lk.gov.ird.etax.edms.entity.Document;
import lk.gov.ird.etax.edms.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/edms")
@CrossOrigin(origins = "*")
public class DocumentController {
    @Autowired
    private DocumentService documentService;
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "etax-edms",
            "version", "1.0.0"
        ));
    }
    @PostMapping("/upload")
    public ResponseEntity<Document> upload(
            @RequestParam String tin,
            @RequestParam String category,
            @RequestParam MultipartFile file) throws Exception {
        return ResponseEntity.ok(documentService.uploadDocument(tin, category, file));
    }
    @GetMapping("/documents/{tin}")
    public ResponseEntity<List<Document>> getDocuments(@PathVariable String tin) {
        return ResponseEntity.ok(documentService.getDocumentsByTin(tin));
    }
    @GetMapping("/documents/{id}/url")
    public ResponseEntity<Map<String, String>> getUrl(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(Map.of("url", documentService.getPresignedUrl(id)));
    }
    @GetMapping("/documents/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {
        byte[] data = documentService.downloadDocument(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "document-" + id);
        return ResponseEntity.ok().headers(headers).body(data);
    }
    @PutMapping("/documents/{id}/verify")
    public ResponseEntity<Document> verify(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.verifyDocument(id));
    }
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
