

@echo off
set DIR=%~dp0
cd %DIR%

rd /s /q %DIR%\simulator
rd /s /q %DIR%\frameworks\runtime-src\proj.android-studio\app\assets
rd /s /q %DIR%\frameworks\runtime-src\proj.android-studio\app\build\outputs


xcopy D:\svn\2dx_seawarex\trunk\client\res %DIR%\res /e /y
xcopy D:\svn\2dx_seawarex\trunk\client\src %DIR%\src /e /y

@echo 拷贝结束了~~开始打包把！！！点击任意键~~~
PAUSE>NUL

cmd /k "cocos compile -s . -p android --proj-dir proj.android-studio --ap android-18 --yes-apk"

