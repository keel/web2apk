# web2apk
Pack h5 or web project to Android APK fast. It's only a simple Android Studio project source code, zero dependency, no libs, no js-bridge, no any other tools.

# Description:
* Support fully cross-domain(by project [ajaxhook](https://github.com/keel/ajaxhook));
* Include a standalone server(NanoHttpd-Core) inside;
* Zero dependency, no libs, no js-bridge;
* Simple and small, the source code only three java files, one js file;
* Totally open project, so you can custom anything you like.
* Author: [Keel](https://github.com/keel) ;

## Useage
1. Build web/h5/h5-game project to a web directory(like "web-mobile");
2. Git clone or download this project;
3. Copy the web directory to "app/src/main/assets/", rename it to "web" to replace the original "web";
4. Copy "app/src/main/assets/ajax-cross.js" to the "web" dir and modify "index.html" to load it first(add "<script src="ajax-cross.js"></script>" in html header, before any other js), so the cross-domain will be available;
5. Set your "app_name" in "app/src/main/res/values/strings.xml";
6. Set your "app_icon.png" in "app/src/main/res/drawable/";
7. Set your "applicationId" in "app/build.gradle", and change the "package" in "app/src/main/AndroidManifest.xml" as the same;
8. Use Android Studio to open this project, and build to APK.

## Custom
Custom local server port or other properties in "src/main/java/com/web2apk/MainActivity".

# 简述:
* 完全解决跨域问题(使用了项目[ajaxhook](https://github.com/keel/ajaxhook));
* 包含一个独立的web服务器(NanoHttpd-Core);
* 零依赖, 无libs, 无js-bridge;
* 小巧简单, 源码就两个JAVA,一个js,还有个NanoHttpd-Core的java源码;
* 完整开源项目,可进行任意定制化;
* 作者: [Keel](https://github.com/keel);

## 使用步骤
1. 将h5/h5游戏/web项目打成web-mobile包或web目录;
2. 使用git clone或直接下载本项目;
3. 将项目web根目录复制到app/src/main/assets/下,替换现有的web目录;
4. 将assets下的ajax-cross.js复制到web目录,并在index.html的header中添加<script src="ajax-cross.js"></script>引用,此步骤为解决跨域问题,需要作为优先于其他js引用;
5. 修改app/src/main/res/values/strings.xml中的app_name为游戏名称;
6. 修改app/src/main/res/drawable/中的app_icon.png为实际游戏图标;
7. 修改app/build.gradle中的applicationId为实际包名,同时将app/src/main/AndroidManifest.xml中的package也改为同样的包名
8. 使用AndroidStudio导入此项目,并打出APK签名包.

## 定制化
修改"src/main/java/com/web2apk/MainActivity"中的相关变量, 可调整内部server的端口等参数.

