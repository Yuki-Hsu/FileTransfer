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
$ java server -h
Usage:
    java server [options]
Options:
    -h, --help Show help
    -4 Make all connections via IPv4
    -6 Make all connections via IPv6
    -p <port> Set the listening port
    -c Use specified conf.properties file to start
    -l List sharing files of current directory
```

### For client

```console
$ java server -h
Usage:
    java server [options]
Options:
    -h, --help Show help
    -4 Make all connections via IPv4
    -6 Make all connections via IPv6
    -p <port> Set the listening port
    -c Use specified conf.properties file to start
    -l List sharing files of server
    -d <file> Download specified sharing file
    -d <directory> Download all files in specified directory
    -u <file> Upload specified file to server
```

## Features
