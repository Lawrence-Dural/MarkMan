package com.markman.core.tenant;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TenantHibernateConfig {

    @Bean
    HibernatePropertiesCustomizer tenantIdentifierResolverCustomizer(
            HibernateTenantIdentifierResolver resolver
    ) {
        return properties -> properties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, resolver);
    }
}
