如何使用Android Studio开发Ticwear应用
=========================================================

所有步骤可参考sample下的DataLayer工程

1. 使用AndroidStudio创建应用，同时选中Phone和Wear平台；

2. 在应用根目录的build.gradle文件中添加Ticwear打包插件：
   classpath 'com.ticwear.tools.build:gradle:1.1.0'

3. 在mobile/build.gradle文件中引用Ticwear打包插件：
   apply plugin: 'com.ticwear.application'

4. 拷贝mobvoi-api.jar到工程中（用于手机和手表数据传输）

5. 如果不需要兼容原生android wear的话，移除gms的依赖
   // compile 'com.google.android.gms:play-services:7.3.0'

6. 开发app的功能代码；

7. 配置release签名文件，使用'gradle :mobile:assembleRelease'命令，或者在AndroidStuido的gradle任务面板中选择:mobile:assembleRelease单击，就可生成最终的发布包了，路径在'mobile/build/outputs/apk/mobile-release.apk'；

8. 开发中碰到任何问题，请发邮件给'ts@mobvoi.com'，或者前往https://github.com/ticwear/sdk/issues创建issues。