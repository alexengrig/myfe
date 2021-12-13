# myfe

[![Build Status](https://app.travis-ci.com/alexengrig/myfe.svg?token=gkPekbuqV4MisskP2zyz&branch=master)](https://app.travis-ci.com/alexengrig/myfe)

MYFE (MYFE Youth File Explorer) is a file explorer application.

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

## License

This project is [licensed](LICENSE) under [Apache License, version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
