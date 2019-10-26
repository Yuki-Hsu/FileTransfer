# FileTransfer

🎞 A command-line interface tool of using Java Socket API transfer files

- [FileTransfer](#filetransfer)
  - [Usage](#usage)
    - [For server](#for-server)
    - [For client](#for-client)
  - [Features](#features)
  - [学习笔记](#%e5%ad%a6%e4%b9%a0%e7%ac%94%e8%ae%b0)

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

## 学习笔记

- TCP 传输存在粘包问题。即发送端分两次发送数据，接收端一次性全部接收数据
- 输出流写完就要刷新。即 `OutputStream.write(buffer, 0, len)` 完成后最好立马 `OutputStream.flush()`。好处如下：
  - Socket 中可以确保一端的 `OutputStream out = socket.getOutputStream()` 输出流的内容可以立马被另一端 `InputStream in = socket.getInputStream()` 输入流取到
  - 文件流中可以确保文件输出流及时变化，即可以及时看到本地文件大小变化
- Socket 中一端关闭输出流 `out.close()` 可以使得另一端的 `in.read(buffer)` 调用得到 `-1`，不然 `in.read(buffer)` 会一直阻塞。
