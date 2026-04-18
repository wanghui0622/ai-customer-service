package com.aics.adminwebmvc.service;

import com.aics.adminwebmvc.dto.RagDocumentItem;
import com.aics.rag.domain.RagDocument;
import com.aics.rag.service.RagIngestionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class RagAdminService {

    private final RagIngestionService ragIngestionService;
    private final CopyOnWriteArrayList<RagDocumentItem> recent = new CopyOnWriteArrayList<>();

    public RagAdminService(RagIngestionService ragIngestionService) {
        this.ragIngestionService = ragIngestionService;
    }

    public RagDocumentItem add(String title, String content) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title 不能为空");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content 不能为空");
        }
        RagDocument doc = ragIngestionService.documents().fromString(title, content);
        int chunks = ragIngestionService.ingest(doc);
        RagDocumentItem item = new RagDocumentItem(doc.id(), title, content.length(), chunks);
        recent.add(item);
        return item;
    }

    public List<RagDocumentItem> list() {
        return List.copyOf(recent);
    }
}
