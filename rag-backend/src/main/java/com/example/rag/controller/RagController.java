package com.example.rag.controller;

import com.example.rag.model.Document;
import com.example.rag.service.ChatService;
import com.example.rag.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RagController {

    private final DocumentService documentService;
    private final ChatService chatService;

    @PostMapping("/documents")
    public ResponseEntity<Document> uploadDocument(@RequestParam("file") MultipartFile file) throws IOException, TikaException {
        log.info("Received file upload request: {}", file.getOriginalFilename());
        Document doc = documentService.uploadDocument(file);
        return ResponseEntity.ok(doc);
    }

    @GetMapping("/documents")
    public ResponseEntity<List<Document>> getDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        log.info("Received delete request for document: {}", id);
        documentService.deleteDocument(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String question = request.get("message");
        log.info("Received chat request: {}", question);
        String answer = chatService.chat(question);
        return ResponseEntity.ok(Map.of("answer", answer));
    }
}
