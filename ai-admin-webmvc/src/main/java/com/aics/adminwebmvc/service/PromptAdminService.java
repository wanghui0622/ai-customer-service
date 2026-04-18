package com.aics.adminwebmvc.service;

import com.aics.adminwebmvc.dto.PromptItemDto;
import com.aics.prompt.factory.PromptFactory;
import com.aics.prompt.factory.PromptScenario;
import com.aics.prompt.factory.PromptVersion;
import com.aics.prompt.template.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PromptAdminService {

    private final PromptFactory promptFactory;
    private final Map<String, PromptItemDto> custom = new ConcurrentHashMap<>();

    public PromptAdminService(PromptFactory promptFactory) {
        this.promptFactory = promptFactory;
    }

    public List<PromptItemDto> listAll() {
        List<PromptItemDto> items = new ArrayList<>();
        for (PromptScenario s : PromptScenario.values()) {
            for (PromptVersion v : PromptVersion.values()) {
                PromptTemplate t = promptFactory.templateFor(s, v);
                String id = "builtin-" + s.name() + "-" + v.name();
                items.add(new PromptItemDto(id, s.name(), v.name(), t.system(), t.user(), true));
            }
        }
        items.addAll(custom.values());
        return items;
    }

    public PromptItemDto save(String id, String scenario, String system, String user) {
        if (system == null || system.isBlank()) {
            throw new IllegalArgumentException("system 不能为空");
        }
        if (user == null || user.isBlank()) {
            throw new IllegalArgumentException("user 不能为空");
        }
        String key = id != null && !id.isBlank() ? id : "custom-" + UUID.randomUUID();
        if (key.startsWith("builtin-")) {
            throw new IllegalArgumentException("不可覆盖内置模板 id（builtin- 前缀保留）");
        }
        String scen = scenario != null && !scenario.isBlank() ? scenario : "CUSTOM";
        PromptItemDto dto = new PromptItemDto(key, scen, "-", system, user, false);
        custom.put(key, dto);
        return dto;
    }
}
