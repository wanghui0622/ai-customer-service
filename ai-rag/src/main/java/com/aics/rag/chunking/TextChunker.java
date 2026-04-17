package com.aics.rag.chunking;

import com.aics.rag.domain.TextChunk;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 固定长度滑动窗口切分，支持 overlap。
 */
@Component
public class TextChunker {

    /**
     * 按配置将全文切成多块。
     *
     * @param text    全文
     * @param options 窗口与重叠
     * @return 非空文本块列表（可能为空列表若输入为空）
     */
    public List<TextChunk> split(String text, ChunkingOptions options) {
        List<TextChunk> out = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return out;
        }
        int size = options.maxChars();
        int overlap = options.overlapChars();
        int step = Math.max(1, size - overlap);
        for (int start = 0; start < text.length(); start += step) {
            int end = Math.min(start + size, text.length());
            String piece = text.substring(start, end).trim();
            if (!piece.isEmpty()) {
                Map<String, String> meta = new HashMap<>();
                meta.put("charStart", String.valueOf(start));
                meta.put("charEnd", String.valueOf(end));
                out.add(new TextChunk(UUID.randomUUID().toString(), piece, meta));
            }
            if (end >= text.length()) {
                break;
            }
        }
        return out;
    }
}
