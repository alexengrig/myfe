# myfe

MYFE (MYFE Youth File Explorer) is a file explorer application.

## Architecture

```
        User
          \ action
           v
          VIEW
          ^   \ event
 notify /      \ 
    MODEL       )
        ^      /
  update \    v
       CONTROLLER
         ^    \
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
