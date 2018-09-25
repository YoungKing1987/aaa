
#include "luabinding/cocos2dx_extra_luabinding.h"

#include "cocos2dx_extra_ex.h"

#ifdef __cplusplus
extern "C" {
#endif


#if defined(LUA_VERSION_NUM) && LUA_VERSION_NUM >= 501

void luaopen_lua_extra(lua_State* tolua_S) 
{
	luaopen_cocos2dx_extra_luabinding(tolua_S);
}

#endif


#ifdef __cplusplus
}
#endif

