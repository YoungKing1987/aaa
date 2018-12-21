
#include "lua_extensions.h"

#ifdef __cplusplus
extern "C" {
#endif

// cjson
#include "cjson/lua_cjson.h"

// zlib
#include "zlib/lua_zlib.h"

// filesystem
#include "filesystem/lfs.h"

// lsqlite3
//#include "lsqlite3/lsqlite3.h"

// lua debugger
#include "debugger/debugger.h"

#include "messagepack/lua_cmsgpack.h"

// luapbc
extern int luaopen_protobufc(lua_State *L);

// luaxml
extern int  luaopen_LuaXML_lib(lua_State* L);

// lua struct
extern int luaopen_struct(lua_State *L);

// lua crypto 
extern int luaopen_ccrypto(lua_State *L);

// lua time
extern int luaopen_ctime(lua_State *L);

// lua crandom
//extern int luaopen_crandom(lua_State *L);

// lua snapshot
extern int luaopen_snapshot(lua_State *L);

// lua battle check token
//extern int luaopen_encbtl(lua_State *L);

// lua bit
//extern int luaopen_bit(lua_State *L);

static luaL_Reg luax_exts[] = {
    {"cjson", luaopen_cjson_safe},
	{ "struct", luaopen_struct},

	{"zlib", luaopen_zlib},
	{"lfs", luaopen_lfs},
    //{"lsqlite3", luaopen_lsqlite3},
    {NULL, NULL}
};

void luaopen_lua_extensions_ex(lua_State *L)
{
    // load extensions
    luaL_Reg* lib = luax_exts;
    lua_getglobal(L, "package");
    lua_getfield(L, -1, "preload");
    for (; lib->func; lib++)
    {
        lua_pushcfunction(L, lib->func);
        lua_setfield(L, -2, lib->name);
    }
    lua_pop(L, 2);

    luaopen_cmsgpack(L);
#ifndef SVR_BUILD
	luaopen_protobufc(L);
#endif
	luaopen_LuaXML_lib(L);
	luaopen_ccrypto(L);
	luaopen_ctime(L);
	//luaopen_crandom(L);
	//luaopen_debugger(L);
	luaopen_snapshot(L);
    //luaopen_encbtl(L);
    //luaopen_bit(L);
}

#ifdef __cplusplus
} // extern "C"
#endif
