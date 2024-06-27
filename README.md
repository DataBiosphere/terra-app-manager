# terra-app-manager

This repo is the Application Manager for Terra.

It is responsible for understanding how to deploy container-based Helm applications into Terra's
Data Plane.

## Environment setup

The following tools are require to interact with this repository.

- java
- docker

To verify your setup, please execute the following command.

```shell
./scripts/setup
```

After running the `setup`-script,

- all other scripts in the `./scripts` directory should be runnable, and
- you should have a container running using the `-database-1` suffix.
  you can connect to this instance by running:
  ```shell
  ./scripts/run-db shell
  ```

## Running the service

There are two flavors for running the service, either `local`-ly or in a `docker` container.
Both flavors of execution have been scripted for your convenience.

Running the `local`-command will get start the `service` locally,
and you should be able to access your running instance at:
- http://localhost:8080

Running the `docker`-command will start the `service` in a docker container,
and you should be able to access your running instance at one of:
- http://localhost:8080
- http://local.dsde-dev.broadinstitute.org/swagger-ui.html

To access the `service` through the `web-proxy`,
you will need to make sure the following has been added to your `/etc/hosts` file.

```shell
...
127.0.0.1       local.dsde-dev.broadinstitute.org
```

### Local execution

```shell
./scripts/run local
```

### Docker-ized execution

```shell
./scripts/run docker
```

## Service swagger endpoints

Existing deployed versions of this service are:

- https://appmanager.dsde-dev.broadinstitute.org

## Next steps

Please read [CONTRIBUTING.md](./CONTRIBUTING.md) for more information about the process of
contributing code to the service and [DESIGN.md](./DESIGN.md) for a deeper understanding
of the repository's structure and design patterns.
