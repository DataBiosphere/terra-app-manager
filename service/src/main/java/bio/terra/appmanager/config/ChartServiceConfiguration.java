package bio.terra.appmanager.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "appmanager.charts")
public record ChartServiceConfiguration(@NotNull @NotEmpty List<String> allowedNames) {}
