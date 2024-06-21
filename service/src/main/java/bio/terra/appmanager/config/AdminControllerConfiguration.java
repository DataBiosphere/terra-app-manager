package bio.terra.appmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "appmanager.admin")
public record AdminControllerConfiguration(String[] serviceAccounts) {}
