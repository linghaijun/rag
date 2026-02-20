package com.example.rag.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "documents")
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(length = 500)
    private String originalFileName;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column
    private Long fileSize;
    
    @Column
    private String fileType;
    
    @Column
    private LocalDateTime uploadTime;
    
    @Column
    private Integer chunkCount;
}
