package com.aics.core.chat;

import com.aics.core.AiCoreAutoConfiguration;
import com.aics.spi.LlmClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AiCoreAutoConfiguration.class)
class ChatServiceTest {

    @Autowired
    private LlmClient llmClient;

    @Test
    void llmClientBeanPresent() {
        assertThat(llmClient).isNotNull();
    }
}
