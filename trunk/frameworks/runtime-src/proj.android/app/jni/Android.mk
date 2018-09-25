LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := cocos2dlua_shared

LOCAL_MODULE_FILENAME := libcocos2dlua

LOCAL_SRC_FILES := \
../../../Classes/AppDelegate.cpp \
../../../Classes/lua_reg_auto.cpp \
hellolua/main.cpp 
#pbc_by_yj
LOCAL_SRC_FILES += ../../../Classes/custom/protocol/pbc/src/alloc.c \
../../../Classes/custom/protocol/pbc/src/array.c \
../../../Classes/custom/protocol/pbc/src/bootstrap.c \
../../../Classes/custom/protocol/pbc/src/context.c \
../../../Classes/custom/protocol/pbc/src/decode.c \
../../../Classes/custom/protocol/pbc/src/map.c \
../../../Classes/custom/protocol/pbc/src/pattern.c \
../../../Classes/custom/protocol/pbc/src/proto.c \
../../../Classes/custom/protocol/pbc/src/register.c \
../../../Classes/custom/protocol/pbc/src/rmessage.c \
../../../Classes/custom/protocol/pbc/src/stringpool.c \
../../../Classes/custom/protocol/pbc/src/varint.c \
../../../Classes/custom/protocol/pbc/src/wmessage.c \
../../../Classes/custom/protocol/pbc/pbc-lua.c \

#ext_by_zyj
LOCAL_SRC_FILES += ../../../Classes/custom/extension/bit/bit.c \
../../../Classes/custom/extension/cencbtl/encbtl.c  \
../../../Classes/custom/extension/cjson/fpconv.c \
../../../Classes/custom/extension/cjson/lua_cjson.c \
../../../Classes/custom/extension/cjson/strbuf.c \
../../../Classes/custom/extension/crandom/crandom.c \
../../../Classes/custom/extension/crypto/crypto.c \
../../../Classes/custom/extension/crypto/qtea.c \
../../../Classes/custom/extension/crypto/uECC.c \
../../../Classes/custom/extension/crypto/xor.c \
../../../Classes/custom/extension/crypto/xxtea.c \
../../../Classes/custom/extension/debugger/debugger.c \
../../../Classes/custom/extension/filesystem/lfs.c \
../../../Classes/custom/extension/lsqlite3/lsqlite3.c \
../../../Classes/custom/extension/lsqlite3/sqlite3.c \
../../../Classes/custom/extension/luasnapshot/snapshot.c \
../../../Classes/custom/extension/luastruct/struct.c \
../../../Classes/custom/extension/luaxml/LuaXML_lib.c \
../../../Classes/custom/extension/messagepack/lua_cmsgpack.c \
../../../Classes/custom/extension/time/ctime.c \
../../../Classes/custom/extension/zlib/lua_zlib.c \
../../../Classes/custom/extension/lua_extensions.c \

#gamehelp_by_zjd
LOCAL_SRC_FILES += ../../../Classes/custom/gamehelp/CCGh.cpp \
../../../Classes/custom/Log/LogMore.cpp \
../../../Classes/custom/Log/ErrorWindow.cpp \
../../../Classes/custom/AStar/AStar.cpp \
../../../Classes/custom/AStar/AStarManager.cpp \
../../../Classes/custom/AStar/BlockAllocator.cpp \
../../../Classes/custom/AStar/Singleton.cpp \
../../../Classes/custom/extra/encode/CCEncode.cpp \
../../../Classes/custom/extra/encode/base64/libbase64.cc \
../../../Classes/custom/extra/encode/md5/md5.cc \
../../../Classes/custom/extra/luabinding/cocos2dx_extra_luabinding.cpp \
../../../Classes/custom/extra/network/CCNetwork.cpp \
../../../Classes/custom/extra/platform/android/CCNativeAndroid.cpp \
../../../Classes/custom/extra/platform/android/CCNetworkAndroid.cpp \
../../../Classes/custom/extra/cocos2dx_extra_ex.cpp \

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../../Classes 
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../Classes/custom/protocol/pbc \
					$(LOCAL_PATH)/../../../Classes/custom/extension \
					$(LOCAL_PATH)/../../../Classes/custom \
					$(LOCAL_PATH)/../../../Classes/custom/extra \

# _COCOS_HEADER_ANDROID_BEGIN
# _COCOS_HEADER_ANDROID_END

LOCAL_STATIC_LIBRARIES := cocos2d_lua_static

# _COCOS_LIB_ANDROID_BEGIN
# _COCOS_LIB_ANDROID_END

include $(BUILD_SHARED_LIBRARY)

$(call import-add-path, $(LOCAL_PATH)/../../../../cocos2d-x)
$(call import-module, cocos/scripting/lua-bindings/proj.android)

# _COCOS_LIB_IMPORT_ANDROID_BEGIN
# _COCOS_LIB_IMPORT_ANDROID_END
