package bio.terra.appmanager.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "appmanager.admin")
public record AdminControllerConfiguration(
    List<String> serviceAccountsForRead, List<String> serviceAccountsForWrite) {}
