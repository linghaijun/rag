package com.example.rag.service;

import com.example.rag.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final VectorStore vectorStore;
    private final ChatClient.Builder chatClientBuilder;
    private final DocumentRepository documentRepository;

    public String chat(String question) {
        if (documentRepository.count() == 0) {
            return "知识库为空，请先上传文档！";
        }
        
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(5)
                .build();
        
        var documents = vectorStore.similaritySearch(searchRequest);
        
        if (documents.isEmpty()) {
            return "未找到相关文档内容，请尝试其他问题或上传更多文档。";
        }
        
        StringBuilder context = new StringBuilder();
        for (var doc : documents) {
            context.append(doc.getText()).append("\n\n");
        }
        
        String systemPrompt = """
            你是一个智能问答助手。请根据提供的上下文信息回答用户的问题。
            如果上下文中没有相关信息，请如实告知用户。
            请用中文回答问题。
            """ + "\n\n上下文信息:\n" + context;
        
        ChatClient chatClient = chatClientBuilder.build();
        ChatResponse response = chatClient.prompt()
                .user(question)
                .system(systemPrompt)
                .call()
                .chatResponse();
        
        return response.getResult().getOutput().getText();
    }

    public Flux<String> chatStream(String question) {
        if (documentRepository.count() == 0) {
            return Flux.just("知识库为空，请先上传文档！");
        }
        
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(5)
                .build();
        
        var documents = vectorStore.similaritySearch(searchRequest);
        
        if (documents.isEmpty()) {
            return Flux.just("未找到相关文档内容，请尝试其他问题或上传更多文档。");
        }
        
        StringBuilder context = new StringBuilder();
        for (var doc : documents) {
            context.append(doc.getText()).append("\n\n");
        }
        
        String systemPrompt = """
            你是一个智能问答助手。请根据提供的上下文信息回答用户的问题。
            如果上下文中没有相关信息，请如实告知用户。
            请用中文回答问题。
            """ + "\n\n上下文信息:\n" + context;
        
        ChatClient chatClient = chatClientBuilder.build();
        return chatClient.prompt()
                .user(question)
                .system(systemPrompt)
                .stream()
                .content();
    }
}
