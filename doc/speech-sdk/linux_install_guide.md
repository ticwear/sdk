### Linux版本
官网下载[Linux版](http://ai.chumenwenwen.com/pages/document/intro?)语音SDK. SDK格式如：*speechsdk-linux-{platform}.zip*
> **注意** 目前仅开放mips平台版，详情参考[介绍#兼容性](../introduce.md)

```sh
.
|-- ...
`-- speechsdk_linux_mips-1492255383893.zip
```
解压到指定目录
```shell
$ cp ~/Downloads/speechsdk-linux-XXX.zip ~/private
$ cd ~/private
$ unzip speechsdk-linux-XXX.zip
```
SDK目录结构如下:
```sh.
|-- libmobvoisdk.so      -> 唯一需链接的共享库
`-- speech_sdk.h         -> 唯一包含的头文件
```
参考官方提供的[demo](linux_code_example.md)进行构建测试
```sh
$ touch test.cpp
$ vim test.cpp
　.. copy demo content & paste into here..
$ export WENWEN_SDK=~/private/speechsdk_mips
$ mips-linux-gnu-g++ -I${WENWEN_SDK} -L${WENWEN_SDK} -lmobvoisdk -o test.out
```

