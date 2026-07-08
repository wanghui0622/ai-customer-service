package com.aics.integrations.spi;

import java.util.Map;

/**
 * 外部系统连接器 SPI：Tool 通过 Adapter 调用，不直接依赖 HTTP/SDK。
 */
public interface IntegrationConnector {

    String id();

    String type();

    boolean supports(String operation);

    IntegrationResponse execute(String operation, IntegrationRequest request);
}
