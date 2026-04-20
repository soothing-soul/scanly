package com.scanly.config.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Central configuration class for customizing the JSON serialization and
 * deserialization behavior across the Scanly application.
 * <p>
 * This configuration ensures that the primary {@link ObjectMapper} utilized by
 * Spring's HTTP message converters is aware of the {@link RecordMethodIgnoringInspector}.
 * This is a key architectural decision to ensure that Java Records—used heavily
 * for DTOs and internal states—behave predictably during API communication,
 * serializing only their canonical components.
 * </p>
 */
@Configuration
public class JsonConfig {

    /**
     * Configures and provides the application-wide {@link ObjectMapper} bean.
     * <p>
     * By utilizing the {@link Jackson2ObjectMapperBuilder}, this bean inherits
     * the standard Spring Boot defaults (like ISO-8601 date formatting) while
     * explicitly registering the custom {@code RecordMethodIgnoringInspector}.
     * This prevents any custom logic or helper methods inside Records from
     * leaking into the public-facing JSON responses.
     *
     * @param builder The {@link Jackson2ObjectMapperBuilder} provided by the
     * Spring context, containing the default settings.
     * @return A fully configured {@link ObjectMapper} instance.
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();

        /*
         * Override the default introspector with our Record-aware version.
         * This acts as a filter during the serialization pipeline, identifying
         * which methods are "pure" record components and which should be ignored.
         */
        objectMapper.setAnnotationIntrospector(new RecordMethodIgnoringInspector());

        return objectMapper;
    }
}