package bio.terra.common.events.config;

import bio.terra.common.events.config.types.BeeConfig;
import bio.terra.common.events.config.types.GoogleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * Based on all the known pubsub configurations, present the logic necessary for the client to
 * connect to the necessary resources.
 */
@Configuration
public class PubsubConfig {

  private static final Logger logger = LoggerFactory.getLogger(PubsubConfig.class);

  public static final String NO_PUBSUB_EMULATOR_HOST_SPECIFIED = "-";
  private String applicationName;
  private BeeConfig beeConfig;
  private GoogleConfig googleConfig;
  private String emulatorTarget;

  public PubsubConfig(
      ApplicationContext applicationContext,
      BeeConfig beeConfig,
      GoogleConfig googleConfig,
      ObjectProvider<String> pubsubEmulatorEndpoint) {
    this.applicationName = applicationContext.getId();
    this.beeConfig = beeConfig;
    this.googleConfig = googleConfig;

    emulatorTarget = determineEmulatorTarget(pubsubEmulatorEndpoint.getIfAvailable());
  }

  public String publishedBy() {
    return applicationName;
  }

  public boolean connectLocal() {
    return emulatorTarget != null;
  }

  public boolean createTopic() {
    return (beeConfig != null && beeConfig.isActive()) || connectLocal();
  }

  public String emulatorTarget() {
    return emulatorTarget;
  }

  public GoogleConfig googleConfig() {
    return googleConfig;
  }

  public String nameSuffix() {
    if (beeConfig != null && beeConfig.isActive()) {
      return beeConfig.name();
    }
    return null;
  }

  private String determineEmulatorTarget(String emulatorTarget) {
    if (emulatorTarget != null) {
      logger.info("Emulator target configured via bean: " + emulatorTarget);
      return emulatorTarget;
    } else if (googleConfig.pubsubEmulatorTargetForEnvironment() != null
        && !googleConfig
            .pubsubEmulatorTargetForEnvironment()
            .equalsIgnoreCase(NO_PUBSUB_EMULATOR_HOST_SPECIFIED)) {
      logger.info(
          "Emulator target configured via environment: "
              + googleConfig.pubsubEmulatorTargetForEnvironment());
      return googleConfig.pubsubEmulatorTargetForEnvironment();
    }
    logger.info("NO pubsub emulator configured - GOING LIVE");
    return null;
  }
}
