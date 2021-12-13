# myfe

[![Build Status](https://app.travis-ci.com/alexengrig/myfe.svg?token=gkPekbuqV4MisskP2zyz&branch=master)](https://app.travis-ci.com/alexengrig/myfe)

MYFE (MYFE Youth File Explorer) is a file explorer application.

![Screenshot](docs/screenshot.png)

## Build

Required Gradle and JDK 11.

```shell
# build
gradle shadowJar
# run
java -jar build/libs/myfe.jar
```

## Architecture

```
        User
          \ action
           v             update
        UI delegate -----------> MODEL
      (VIEW + CONTROLLER) <-------
          ^    \           notify
           \    v
           SERVICE
            ^   / query
    result /   v
        REPOSITORY
          ^    \
           \    v
          Data Store
```

## UI

### Window

![Window](docs/window.png)

### Tab

![Tab](docs/tab.png)

## License

This project is [licensed](LICENSE) under [Apache License, version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
