# Event library

The event library is located at `bio.terra.common.events`.

## Frequently Asked Questions

### How do I introduce a new event stream into the system?

There are multiple steps required to do this, both in terms of system configuration and in code.

System configuration of new events are done through the `terraform-ap-deployments/` repository.

For more information about adding your event stream into the system, please refer to the following [README](https://github.com/broadinstitute/terraform-ap-deployments/tree/master/platform-event-topics).

### How do I configure my service to publish or subscribe to an existing event stream? 

#### Platform level configuration

Again, we are back in terraform-ap-deployments/, and this assumes you've completed [adding the event stream](#how-do-i-introduce-a-new-event-stream-into-the-system) to the platform already.

This requires that your module/service is managed by the `terraform-ap-deployments/` repository, and a migration from `terraform-ap-modules/` may be required. Please talk to [#dsp-devops-champions](https://broadinstitute.slack.com/archives/CADM7MZ35) in slack.

Assuming your service is managed from the terraform-ap-deployments/ repository, you now need to configure your service to publish and subscribe to events. Additional information can be found in the [terraform-ap-deployments/modules/pubsub-service-events](https://github.com/broadinstitute/terraform-ap-deployments/tree/master/platform-event-topics) README. 

#### Library level configuration

Library configuration to connect to our pubsub infrastructure should be consistent across all services. This section details the necessary property configurations, be it via `.yml` or `.properties`.

The following `terra.common` properties need to be configured in order to use the library and connect to various environments. Descriptions of the properties are detailed below. The `env.` properties are shown below as items that are useful to override based on specific environment setups. 

```yml
env:
  google:
    project_id: ${SERVICE_GOOGLE_PROJECT:broad-dsde-dev}
    # set value to connect to local pubsub emulator for local development,
    # otherwise set to a '-'-value to connect to live environment specified by `project_id`
    pubsub_emulator_target: ${PUBSUB_EMULATOR_HOST:-}
  bee:
    name: ${BEE_NAME:not-a-bee-env}
    is_active: ${IS_BEE:false}
...
terra.common:
  google:
    project_id: ${env.google.project_id}
    pubsub_emulator_target_for_environment: ${env.google.pubsub_emulator_target}
  bee:
    is_active: ${env.bee.is_active}
    name: ${env.bee.name}
```

| property                                                     | description                                                                                                                                                                                                                                                                                                                                                                | required |
|:-------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------:|
| `terra.common.google.project_id`                             | The google `project_id` to connect to.<br/>In the configuration above, this is configured via the environment variable `SERVICE_GOGLE_PROJECT`                                                                                                                                                                                                                             |    Y     |
| `terra.common.google.pubsub_emulator_target_for_environment` | Set for local (non-live) configuration of a [Google's Pubsub Emulator](https://cloud.google.com/pubsub/docs/emulator).<br/>The `.google.project_id` should be set to the same value identified to start the emulator. If this is not set, and the emulator is not configured to start for tests, the live environment identified by the `.google.project_id` will be used. |    N     |
| `terra.common.bee.is_active`                                 | tells the event library that the runtime environment is our bee environment setup. this will require that the library create topics and subscriptions at startup using a consistent naming (to avoid conflicts)                                                                                                                                                            |    N     |
| `terra.common.bee.name`                                      | the name of the bee environment to ensure topic and subscription names do not intersect                                                                                                                                                                                                                                                                                    |    N     |

### How do I use the library to publish events?

> This requires that someone has completed the following steps:
>
> - the pubsub [topic has been created](#how-do-i-introduce-a-new-event-stream-into-the-system) in the live environments, and
> - your service has been configured to use the topics
>   - by [granting permissions in the live environments](#platform-level-configuration), and
>   - by [configuring your service](#library-level-configuration) to use the library


### How do I use the library to subscribe to events?

> This requires that someone has completed the following steps:
>
> - the pubsub [topic has been created](#how-do-i-introduce-a-new-event-stream-into-the-system) in the live environments, and
> - your service has been configured to use the topics
    >   - by [granting permissions in the live environments](#platform-level-configuration), and
>   - by [configuring your service](#library-level-configuration) to use the library
