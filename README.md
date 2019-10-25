# FileTransfer

🎞 A command-line interface tool of using Java Socket API transfer files

- [FileTransfer](#filetransfer)
  - [Usage](#usage)
    - [For server](#for-server)
    - [For client](#for-client)
  - [Features](#features)

## Usage

> **Note**: JDK java runtime environment must be installed

### For server

```console
$ java Server -h
A tool of starting share files in current directory
Usage:
    java Server [options]
Options:
                  When no option specified, start service on default port: 2019
    -h, --help    Show help and quit
    -p <port>     Set the listening port and start service
    -l            List sharing files of current directory
```

### For client

```console
$ java Client -h
A tool for download/upload files from remote sharing directory
Usage:
    java Client <options>
Options:
    -h, --help    Show help
    -4            Make all connections via IPv4
    -6            Make all connections via IPv6
    -c            Use conf.properties file of current directory to start
    -l            List sharing files of remote host
    -d <file>     Download specified sharing file from remote host
    -u <file>     Upload specified file to remote host (you can drag the file to command prompt)
```

## Features
