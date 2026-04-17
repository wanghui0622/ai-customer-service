package com.aics.tools.builtin;

import com.aics.tools.Tool;
import org.springframework.stereotype.Component;

/**
 * 模拟天气查询：根据输入中是否含城市名返回固定模板（未接真实气象 API）。
 */
@Component
public class WeatherTool implements Tool {

    public static final String NAME = "weather_query";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String description() {
        return "查询某地天气概况（演示数据）。";
    }

    @Override
    public String inputSchemaJson() {
        return """
                {"type":"object","properties":{"location":{"type":"string"}},"required":["location"]}
                """.trim();
    }

    @Override
    public String execute(String input) {
        String city = "未知城市";
        if (input != null) {
            String[] hints = {"北京", "上海", "广州", "深圳", "杭州", "成都"};
            for (String h : hints) {
                if (input.contains(h)) {
                    city = h;
                    break;
                }
            }
        }
        return """
                {"tool":"weather_query","city":"%s","condition":"多云","tempC":22,"humidity":"60%%","note":"演示数据"}
                """.formatted(city).trim();
    }
}
