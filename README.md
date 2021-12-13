# myfe

[![Build Status](https://app.travis-ci.com/alexengrig/myfe.svg?token=gkPekbuqV4MisskP2zyz&branch=master)](https://app.travis-ci.com/alexengrig/myfe)

MYFE (MYFE Youth File Explorer) is a file explorer application.

![Screenshot](docs/screenshot.png)

- view the structure and contents of directories on the local disk, on FTP servers and inside ZIP archives;
- show preview files and images;
- filter files in a directory by a type.

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

## Dependencies

- [Apache Commons Net](https://github.com/apache/commons-net)
- [FlatLaf IntelliJ Themes Pack](https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-intellij-themes)
- [SLF4J](https://github.com/qos-ch/slf4j)
- [Logback](https://github.com/qos-ch/logback)

## License

This project is [licensed](LICENSE) under [Apache License, version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
