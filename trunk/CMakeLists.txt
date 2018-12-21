#/****************************************************************************
# Copyright (c) 2013-2014 cocos2d-x.org
#
# http://www.cocos2d-x.org
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:

# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
# ****************************************************************************/
cmake_minimum_required(VERSION 3.6)

set(APP_NAME MyLuaGame)

project(${APP_NAME})

set(RUNTIME_SRC_ROOT ${CMAKE_CURRENT_SOURCE_DIR}/frameworks/runtime-src)
set(COCOS2DX_ROOT_PATH ${CMAKE_CURRENT_SOURCE_DIR}/frameworks/cocos2d-x)
set(CMAKE_MODULE_PATH ${COCOS2DX_ROOT_PATH}/cmake/Modules/)

include(CocosBuildSet)
set(BUILD_LUA_LIBS ON)
add_subdirectory(${COCOS2DX_ROOT_PATH}/cocos ${ENGINE_BINARY_PATH}/cocos/core)

# script and source files info, not need to compile
set(res_res_folders
    "${CMAKE_CURRENT_SOURCE_DIR}/res"
    )
set(res_src_folders
    "${CMAKE_CURRENT_SOURCE_DIR}/src"
    )
set(res_script_folders
    "${COCOS2DX_ROOT_PATH}/cocos/scripting/lua-bindings/script"
    )
if(APPLE OR VS)
    cocos_mark_multi_resources(res_res RES_TO "Resources/res" FOLDERS ${res_res_folders})
    cocos_mark_multi_resources(res_src RES_TO "Resources/src" FOLDERS ${res_src_folders})
    cocos_mark_multi_resources(res_script RES_TO "Resources/src/cocos" FOLDERS ${res_script_folders})
    set(cc_common_res ${res_res} ${res_src} ${res_script})
endif()

# record sources, headers
set(GAME_HEADER ${RUNTIME_SRC_ROOT}/Classes/AppDelegate.h
${RUNTIME_SRC_ROOT}/Classes/lua_reg_auto.hpp)
set(GAME_SOURCE ${RUNTIME_SRC_ROOT}/Classes/AppDelegate.cpp
${RUNTIME_SRC_ROOT}/Classes/lua_reg_auto.cpp)

# sources need to compile info, include dirs and source files
include_directories(
        ${RUNTIME_SRC_ROOT}/Classes
        ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc
        ${RUNTIME_SRC_ROOT}/Classes/custom/extension
        ${RUNTIME_SRC_ROOT}/Classes/custom
        ${COCOS2DX_ROOT_PATH}/cocos/scripting/lua-bindings/auto
        ${COCOS2DX_ROOT_PATH}/cocos/scripting/lua-bindings/manual
        ${COCOS2DX_ROOT_PATH}/external/lua/luajit/include
        ${COCOS2DX_ROOT_PATH}/external/lua/tolua
        ${COCOS2DX_ROOT_PATH}/tools/simulator/libsimulator/lib
)
if(ANDROID)
    # change APP_NAME to the share library name for Android, it's value depend on AndroidManifest.xml
    set(APP_NAME cocos2dlua)
    list(APPEND GAME_SOURCE ${RUNTIME_SRC_ROOT}/proj.android/app/jni/hellolua/main.cpp)
    include_directories(
         ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension
         ${RUNTIME_SRC_ROOT}/Classes/custom
         )
    list(APPEND GAME_SOURCE
         ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc/src/alloc.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc/src/array.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc/src/bootstrap.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc/src/context.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc/src/decode.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc/src/map.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc/src/pattern.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc/src/proto.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc/src/register.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc/src/rmessage.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc/src/stringpool.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc/src/varint.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc/src/wmessage.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/protocol/pbc/pbc-lua.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/cjson/fpconv.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/cjson/lua_cjson.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/cjson/strbuf.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/crypto/crypto.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/crypto/qtea.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/crypto/uECC.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/crypto/xor.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/crypto/xxtea.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/debugger/debugger.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/filesystem/lfs.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/luasnapshot/snapshot.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/luastruct/struct.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/luaxml/LuaXML_lib.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/messagepack/lua_cmsgpack.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/time/ctime.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/zlib/lua_zlib.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/extension/lua_extensions.c 
         ${RUNTIME_SRC_ROOT}/Classes/custom/gamehelp/CCGh.cpp 
         ${RUNTIME_SRC_ROOT}/Classes/custom/Log/LogMore.cpp 
         ${RUNTIME_SRC_ROOT}/Classes/custom/Log/ErrorWindow.cpp 
          )
elseif(LINUX)
    list(APPEND GAME_SOURCE ${RUNTIME_SRC_ROOT}/proj.linux/main.cpp)
elseif(WINDOWS)
    list(APPEND GAME_HEADER
         ${RUNTIME_SRC_ROOT}/proj.win32/main.h
         ${RUNTIME_SRC_ROOT}/proj.win32/stdafx.h
         ${RUNTIME_SRC_ROOT}/proj.win32/SimulatorWin.h
         ${RUNTIME_SRC_ROOT}/proj.win32/targetver.h
         ${RUNTIME_SRC_ROOT}/proj.win32/resource.h
         )
    list(APPEND GAME_SOURCE
         ${RUNTIME_SRC_ROOT}/proj.win32/game.rc
         ${RUNTIME_SRC_ROOT}/proj.win32/stdafx.cpp
         ${RUNTIME_SRC_ROOT}/proj.win32/main.cpp
         ${RUNTIME_SRC_ROOT}/proj.win32/SimulatorWin.cpp
         ${cc_common_res}
         )
elseif(APPLE)
    if(IOS)
        list(APPEND GAME_HEADER
             ${RUNTIME_SRC_ROOT}/proj.ios_mac/ios/AppController.h
             ${RUNTIME_SRC_ROOT}/proj.ios_mac/ios/RootViewController.h
             )
        set(APP_UI_RES
            ${RUNTIME_SRC_ROOT}/proj.ios_mac/ios/LaunchScreen.storyboard
            ${RUNTIME_SRC_ROOT}/proj.ios_mac/ios/LaunchScreenBackground.png
            ${RUNTIME_SRC_ROOT}/proj.ios_mac/ios/Images.xcassets
            )
        list(APPEND GAME_SOURCE
             ${RUNTIME_SRC_ROOT}/proj.ios_mac/ios/main.m
             ${RUNTIME_SRC_ROOT}/proj.ios_mac/ios/AppController.mm
             ${RUNTIME_SRC_ROOT}/proj.ios_mac/ios/RootViewController.mm
             ${RUNTIME_SRC_ROOT}/proj.ios_mac/ios/Prefix.pch
             ${RUNTIME_SRC_ROOT}/proj.ios_mac/ios/build-cfg.json
             ${APP_UI_RES}
             )
    elseif(MACOSX)
        list(APPEND GAME_HEADER
             ${RUNTIME_SRC_ROOT}/proj.ios_mac/mac/SimulatorApp.h
             ${RUNTIME_SRC_ROOT}/proj.ios_mac/mac/ConsoleWindowController.h
             )
        set(APP_UI_RES
            ${RUNTIME_SRC_ROOT}/proj.ios_mac/mac/Base.lproj/MainMenu.xib
            ${RUNTIME_SRC_ROOT}/proj.ios_mac/mac/en.lproj/MainMenu.xib
            ${RUNTIME_SRC_ROOT}/proj.ios_mac/mac/zh-Hans.lproj/MainMenu.xib
            ${RUNTIME_SRC_ROOT}/proj.ios_mac/mac/ConsoleWindow.xib
            ${RUNTIME_SRC_ROOT}/proj.ios_mac/mac/Icon.icns
            ${RUNTIME_SRC_ROOT}/proj.ios_mac/mac/Info.plist
            )
        list(APPEND GAME_SOURCE
             ${RUNTIME_SRC_ROOT}/proj.ios_mac/mac/main.m
             ${RUNTIME_SRC_ROOT}/proj.ios_mac/mac/SimulatorApp.mm
             ${RUNTIME_SRC_ROOT}/proj.ios_mac/mac/ConsoleWindowController.m
             ${RUNTIME_SRC_ROOT}/proj.ios_mac/mac/Prefix.pch
             ${APP_UI_RES}
             )
    endif()
    list(APPEND GAME_SOURCE ${cc_common_res})
endif()

set(APP_SRC ${GAME_HEADER} ${GAME_SOURCE})

# mark app complie info and libs info
if(NOT ANDROID)
    add_executable(${APP_NAME} ${APP_SRC})
else()
    add_library(${APP_NAME} SHARED ${APP_SRC})
    add_subdirectory(${COCOS2DX_ROOT_PATH}/cocos/platform/android ${ENGINE_BINARY_PATH}/cocos/platform)
    target_link_libraries(${APP_NAME} -Wl,--whole-archive cpp_android_spec -Wl,--no-whole-archive)
endif()

target_link_libraries(${APP_NAME} luacocos2d)
target_include_directories(${APP_NAME} PRIVATE ${RUNTIME_SRC_ROOT}/Classes)

if(WINDOWS OR MACOSX)
    add_subdirectory(${COCOS2DX_ROOT_PATH}/tools/simulator/libsimulator ${ENGINE_BINARY_PATH}/cocos/simulatorlib)
    target_link_libraries(${APP_NAME} simulatorlib)
endif()

# mark app resources, resource will be copy auto after mark
setup_cocos_app_config(${APP_NAME})
if(APPLE)
    set_target_properties(${APP_NAME} PROPERTIES RESOURCE "${APP_UI_RES}")
    if(MACOSX)
        set_target_properties(${APP_NAME} PROPERTIES
                              MACOSX_BUNDLE_INFO_PLIST "${RUNTIME_SRC_ROOT}/proj.ios_mac/mac/Info.plist"
                              )
    elseif(IOS)
        cocos_pak_xcode(${APP_NAME} INFO_PLIST "iOSBundleInfo.plist.in")
        set_xcode_property(${APP_NAME} ASSETCATALOG_COMPILER_APPICON_NAME "AppIcon")
    endif()
elseif(WINDOWS)
    cocos_copy_target_dll(${APP_NAME} COPY_TO ${APP_RES_DIR}/..)
endif()
# copy resource on linux or WINDOWS
if(LINUX OR WINDOWS)
    cocos_copy_res(COPY_TO ${APP_RES_DIR}/res FOLDERS ${res_res_folders})
    cocos_copy_res(COPY_TO ${APP_RES_DIR}/src FOLDERS ${res_src_folders})
    cocos_copy_res(COPY_TO ${APP_RES_DIR}/src/cocos FOLDERS ${res_script_folders})
endif()
