package com.aics.rag.ingestion;

import com.aics.rag.domain.RagDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 将原始输入规范为 {@link RagDocument}，便于后续切分与入库。
 */
@Service
public class DocumentIngestionService {

    /**
     * 从内存字符串构建文档。
     *
     * @param title   展示用标题
     * @param content 正文
     * @return 文档对象
     */
    public RagDocument fromString(String title, String content) {
        Map<String, String> meta = new HashMap<>();
        meta.put("source", "string");
        return new RagDocument(UUID.randomUUID().toString(), title, content, meta);
    }

    /**
     * 从本地文件（UTF-8）读取全文。
     *
     * @param path 文件路径
     * @return 文档对象，元数据中包含 {@code source=file} 与绝对路径
     * @throws IOException 读取失败
     */
    public RagDocument fromFile(Path path) throws IOException {
        String text = Files.readString(path, StandardCharsets.UTF_8);
        Map<String, String> meta = new HashMap<>();
        meta.put("source", "file");
        meta.put("path", path.toAbsolutePath().toString());
        String title = path.getFileName() != null ? path.getFileName().toString() : "file";
        return new RagDocument(UUID.randomUUID().toString(), title, text, meta);
    }
}
