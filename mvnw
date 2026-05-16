#!/usr/bin/env sh
set -e

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
PROPERTIES_FILE="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"
MAVEN_USER_HOME=${MAVEN_USER_HOME:-"$HOME/.m2"}
DISTS_DIR="$MAVEN_USER_HOME/wrapper/dists"

if [ ! -f "$PROPERTIES_FILE" ]; then
  echo "Cannot find $PROPERTIES_FILE" >&2
  exit 1
fi

DISTRIBUTION_URL=$(sed -n 's/^distributionUrl=//p' "$PROPERTIES_FILE")
ARCHIVE_NAME=${DISTRIBUTION_URL##*/}
MAVEN_DIR_NAME=${ARCHIVE_NAME%-bin.zip}
MAVEN_DIR="$DISTS_DIR/$MAVEN_DIR_NAME"
ARCHIVE_PATH="$DISTS_DIR/$ARCHIVE_NAME"

if [ ! -x "$MAVEN_DIR/bin/mvn" ]; then
  mkdir -p "$DISTS_DIR"

  if [ ! -f "$ARCHIVE_PATH" ]; then
    echo "Downloading Maven from $DISTRIBUTION_URL"
    if command -v curl >/dev/null 2>&1; then
      curl -fsSL "$DISTRIBUTION_URL" -o "$ARCHIVE_PATH"
    elif command -v wget >/dev/null 2>&1; then
      wget -q "$DISTRIBUTION_URL" -O "$ARCHIVE_PATH"
    else
      echo "Neither curl nor wget is available to download Maven." >&2
      exit 1
    fi
  fi

  unzip -q "$ARCHIVE_PATH" -d "$DISTS_DIR"
fi

exec "$MAVEN_DIR/bin/mvn" "$@"
