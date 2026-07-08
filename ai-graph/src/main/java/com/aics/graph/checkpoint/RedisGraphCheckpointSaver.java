package com.aics.graph.checkpoint;

import org.bsc.langgraph4j.checkpoint.BaseCheckpointSaver;
import org.bsc.langgraph4j.checkpoint.MemorySaver;

/**
 * Redis checkpoint 占位：当前委托 {@link MemorySaver}，生产可替换为 Redis 实现。
 */
public class RedisGraphCheckpointSaver extends MemorySaver implements BaseCheckpointSaver {
}
