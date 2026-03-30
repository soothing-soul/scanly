package com.scanly.iam.policy.email.impl;

import com.scanly.iam.policy.email.EmailPolicy;
import com.scanly.iam.policy.email.model.EmailContext;
import com.scanly.iam.policy.model.PolicyValidationResult;
import com.scanly.iam.policy.model.PolicyValidationStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * An implementation of {@link EmailPolicy} that restricts email addresses based
 * on their domain name.
 * <p>
 * This policy is typically used to block disposable email services, competitor
 * domains, or known sources of spam. It extracts the domain from the provided
 * {@link EmailContext} and compares it against a defined blacklist.
 */
@Component
public class BlacklistedDomainEmailPolicy implements EmailPolicy {
    private final String[] blacklistedDomains = new String[]{};

    /**
     * Validates the domain of the email address provided in the context.
     *
     * @param emailContext The context containing the email address to check.
     * @return A {@link PolicyValidationResult} indicating success if the domain is
     * allowed, or failure if the domain is present on the blacklist.
     */
    @Override
    public PolicyValidationResult validate(EmailContext emailContext) {
        String email = emailContext.email();

        if (isDomainBlacklisted(email)) {
            return new PolicyValidationResult(
                    PolicyValidationStatus.FAILURE,
                    "EMAIL_DOMAIN_BLACKLISTED",
                    String.format("Domain of the given email %s is blacklisted", email)
            );
        }

        return new PolicyValidationResult();
    }

    /**
     * Checks the domain of the given email against all blacklisted domains available.
     * @param email
     * @return {@code true} if the domain is blacklisted
     */
    private boolean isDomainBlacklisted(String email) {
        String domain = getDomain(email);
        return Arrays.stream(blacklistedDomains)
                .anyMatch(d -> d.equals(domain));
    }

    /**
     * Extracts the domain portion of an email address.
     * <p>
     * For an email such as {@code user@example.com}, this method returns
     * {@code example.com}. It handles parsing by splitting the string at the
     * '@' symbol.
     *
     * @param email The full email address string.
     * @return The domain portion of the address.
     */
    private String getDomain(String email) {
        String[] parts = email.split("@");
        String domain = parts[parts.length - 1];
        return domain;
    }
}