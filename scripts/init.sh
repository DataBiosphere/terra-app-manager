#!/usr/bin/env bash
# set up logging utilities
source $(dirname $0)/utils.sh

# shared variables across all scripts
ROOT_DIR="$(cd "$(dirname "$0")/.."; pwd)"
SCRIPTS_DIR=${SCRIPTS_DIR:-${ROOT_DIR}/scripts}
TMP_DIR=${TMP_DIR:-${ROOT_DIR}/tmp}

DEBUG_OPTS=""

# for CI (in GitHub Actions (GHA), we want to enable different options)
GRADLE_OPTS=${GRADLE_OPTS}
if [ "$CI" == "true" ]; then
  GRADLE_OPTS="--build-cache"
else
  SERVICE_ACCOUNT_ADMINS_FOR_READ="${USER}@broadinstitute.org"
  SERVICE_ACCOUNT_ADMINS_FOR_WRITE="${USER}@broadinstitute.org"
fi
