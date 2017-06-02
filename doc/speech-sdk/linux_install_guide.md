### Linux版本
官网下载[Linux版](http://ai.chumenwenwen.com/pages/document/intro?)语音SDK。SDK格式如：*speechsdk-linux-{version}.zip*  


解压到指定目录
```shell
$ cp ~/Downloads/speechsdk-linux-XXX.zip ~/private
$ cd ~/private
$ unzip speechsdk-linux-XXX.zip
```
SDK目录结构如下:
```sh.
|-- ...
|-- armv7
|   |-- libmobvoisdk.so            -- armv7版核心依赖共享库
|   `-- ..lib-some-3rd-dep.so..    -- armv7版第三方共享库
|-- armv8
|   |-- libmobvoisdk.so            -- armv8版核心依赖共享库
|   `-- ..lib-some-3rd-dep.so..    -- armv8版第三方共享库　
|-- mips
|   `-- libmobvoisdk.so            -- mips版核心依赖共享库　
|-- x86
|   |-- libmobvoisdk.so            -- x86版核心依赖共享库
|   `-- ..lib-some-3rd-dep.so..    -- x86版第三方共享库
 `-- speech-sdk.h                  --　唯一需要包含的头文件
```
参考官方提供的[demo](linux_code_example.md)进行构建测试
> 注意此处仅以mips平台编译为例，且假设当前交叉编译环境的C++代码编译程序为**mips-linux-gnu-g++**

```sh
$ touch test.cpp
$ vim test.cpp
　.. copy demo content & paste into here..
$ export WENWEN_SDK=~/private/mips
$ mips-linux-gnu-g++ -I${WENWEN_SDK} -L${WENWEN_SDK} -lmobvoisdk -o test.out
```

