# Deployment of `appmanager`

`appmanager` is deployed as a container in k8s and
has dependencies on the following downstream services:

* postresql

It is designed to function in Terra's Control Plane.

For more details about how to deploy and configure `appmanager`,
please refer to the helm charts in
[broadinstitute/terra-helmfile](https://github.com/broadinstitute/terra-helmfile/tree/master/charts/appmanager).
