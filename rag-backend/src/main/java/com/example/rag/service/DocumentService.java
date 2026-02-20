package com.example.rag.service;

import com.example.rag.model.Document;
import com.example.rag.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final VectorStore vectorStore;
    private final Tika tika = new Tika();
    private final TokenTextSplitter textSplitter = new TokenTextSplitter(
        500, 100, 5, 10000, true
    );

    private static final int MAX_CONTENT_LENGTH = 100000;

    public Document uploadDocument(MultipartFile file) throws IOException, TikaException {
        log.info("Starting to parse file: {}, size: {} bytes", file.getOriginalFilename(), file.getSize());
        
        String content;
        try (InputStream is = file.getInputStream()) {
            tika.setMaxStringLength(MAX_CONTENT_LENGTH);
            content = tika.parseToString(is);
        } catch (Exception e) {
            log.error("Failed to parse file: {}", file.getOriginalFilename(), e);
            throw e;
        }
        
        log.info("Parsed content length: {}", content != null ? content.length() : 0);
        
        Document doc = new Document();
        doc.setFileName(file.getOriginalFilename());
        doc.setOriginalFileName(file.getOriginalFilename());
        doc.setContent(content);
        doc.setFileSize(file.getSize());
        doc.setFileType(file.getContentType());
        doc.setUploadTime(LocalDateTime.now());
        
        doc = documentRepository.save(doc);
        
        // 写入向量数据库
        if (content != null && !content.isEmpty()) {
            List<org.springframework.ai.document.Document> chunks = textSplitter.split(
                new org.springframework.ai.document.Document(content)
            );
            for (int i = 0; i < chunks.size(); i++) {
                org.springframework.ai.document.Document chunk = chunks.get(i);
                chunk.getMetadata().put("docId", doc.getId().toString());
                chunk.getMetadata().put("fileName", doc.getFileName());
                chunk.getMetadata().put("chunkIndex", i);
            }
            vectorStore.add(chunks);
            doc.setChunkCount(chunks.size());
            doc = documentRepository.save(doc);
            log.info("Added {} chunks to vector store", chunks.size());
        }
        
        log.info("Document saved: {}, chunks: {}", doc.getFileName(), doc.getChunkCount());
        return doc;
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAllByOrderByUploadTimeDesc();
    }

    public void deleteDocument(Long id) {
        Document doc = documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found: " + id));
        
        // 删除向量数据
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        vectorStore.delete(b.eq("docId", id.toString()).build());
        log.info("Deleted vectors for document: {}", id);
        
        // 删除数据库记录
        documentRepository.delete(doc);
        log.info("Deleted document: {}", doc.getFileName());
    }
}
