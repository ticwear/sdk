# 如何添加更多文档

此目录存放和组织 Ticwear 开发相关的文档，需用Markdown格式撰写。

文档存放在此目录，或此目录下的子目录中。

图片、文件等文档相关附件存放在 `/assets` 中。（之所以使用assets，而不是常用的art，是为了可以匹配开发者网站的目录结构，增加新文件时，大家也可以注意这个问题）

推荐使用一款开源的在线 Markdown 编辑器 [StackEdit][stackedit]，它可以所见即所得的实时渲染预览文档，且提供兼容 GitHub 的 Markdown 格式。并且，它还可以便捷的输出转换后的 HTML 代码（或文件）。

[《开发兼容Ticwear和Android Wear的应用》][gms-compat]就是通过这样的方式部署到[开发者网站上][gms-compat-dev]的。

具体的操作流程是这样的：

1. 使用[StackEdit][stackedit]或直接在GitHub上编写文档
2. 上传文档到 GitHub [Ticwear SDK][ticwear-sdk] 项目中
3. 使用[StackEdit][stackedit]将文档转换成HTML
4. 上传HTML文档到开发者网站。

其中，3、4两步，可以由维护开发者网站的人员来操作。

[stackedit]: https://stackedit.io/
[ticwear-sdk]: https://github.com/ticwear/sdk
[gms-compat]: /doc/gms-compact.md
[gms-compat-dev]: http://developer.ticwear.com/doc/gms-compat
