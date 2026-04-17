package com.aics.tools.registry;

import com.aics.tools.Tool;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具注册中心：启动时注入所有 {@link Tool} Bean，按 {@link Tool#name()} 索引。
 * <p>
 * 扩展方式：新增 {@link Tool} 实现并加入 Spring 容器即可，无需修改注册表代码。
 */
@Component
public class ToolRegistry {

    private final Map<String, Tool> byName = new ConcurrentHashMap<>();

    public ToolRegistry(List<Tool> tools) {
        if (tools != null) {
            for (Tool t : tools) {
                Tool prev = byName.putIfAbsent(t.name(), t);
                if (prev != null) {
                    throw new IllegalStateException("Duplicate tool name: " + t.name());
                }
            }
        }
    }

    public Optional<Tool> get(String name) {
        return Optional.ofNullable(byName.get(name));
    }

    public Collection<Tool> all() {
        return Collections.unmodifiableCollection(byName.values());
    }

    public boolean contains(String name) {
        return byName.containsKey(name);
    }
}
