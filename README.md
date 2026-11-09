# CLion HTML Render 插件

让 CLion 像渲染 Markdown 一样直接渲染 HTML：打开 .html 文件即显示渲染页面（JCEF 内核），
编辑器工具栏自带「仅编辑 / 分屏 / 仅预览」切换按钮和「刷新」按钮，保存源码后预览也会自动刷新。

## 构建

双击 `构建.bat`（首次构建会自动下载 Gradle 8.12.1 及依赖，需要联网，约几分钟）。
构建成功后插件包位于 `build\distributions\clion-html-render-1.0.1.zip`。

## 安装

1. CLion → `Settings (Ctrl+Alt+S)` → `Plugins`
2. 点右上角齿轮 → `Install Plugin from Disk...`
3. 选择上一步生成的 zip，重启 CLion

## 使用

- 在项目树双击任意 .html 文件 → 直接以渲染视图打开
- 编辑器右上角按钮可切换：仅编辑 / 左右分屏 / 仅预览
- 点击 **刷新** 按钮（↻）立即重载页面，无需保存
- 在源码里修改并保存（Ctrl+S）后，预览约 300ms 内自动刷新

## 已知限制

- 只作用于本地文件系统内的 .html 文件
- file:// 协议下页面内的 fetch/XHR 会被浏览器同源策略限制（自包含的 SVG/JS 页面不受影响）
- 基于 IntelliJ 平台内部基类 TextEditorWithPreviewProvider，仅适配 CLion 2026.2.x（build 262.*）


## 开源与发布

- License: MIT（见 LICENSE）
- 市场信息：vendor jiweidada（https://github.com/jiweidada / 1572862469@qq.com）
- 插件 ID：com.jiweidada.htmlrender

其他机器构建：设置环境变量 CLION_SDK_PATH 指向一个本地 CLion 安装目录（或其副本），
然后运行 gradlew buildPlugin（需要 JDK 21）。
