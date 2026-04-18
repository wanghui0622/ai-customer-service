package com.aics.adminwebmvc.dto;

/**
 * 已导入文档摘要（进程内登记，重启后列表清空但向量库可能仍由实现决定）。
 */
public record RagDocumentItem(String documentId, String title, int contentLength, int vectorChunks) {
}
