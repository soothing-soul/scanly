package com.scanly.infra.notification.email.message;

import com.scanly.infra.notification.email.model.EmailTemplateType;
import java.util.Map;

/**
 * A data transfer object (DTO) representing the structured content of an email.
 * <p>
 * This record is used to group the core components of an email—the template layout
 * to be used, and the dynamic data required to fill that template.
 * </p>
 *
 * @param templateType      The {@link EmailTemplateType} specifying which HTML/text
 * template to use for rendering.
 * @param templateVariables A map of key-value pairs used to replace placeholders
 * within the chosen template.
 * @author Scanly Infrastructure Team
 */
public record EmailContent(
        EmailTemplateType templateType,
        Map<String, String> templateVariables
) {}