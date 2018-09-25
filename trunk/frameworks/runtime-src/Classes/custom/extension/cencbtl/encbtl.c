#ifdef __cplusplus
extern "C" {
#endif
#include "lua.h"
#include "lualib.h"
#include "lauxlib.h"
#ifdef __cplusplus
}
#endif

#include "../crypto/xxtea.h"
#include "../crandom/crandom.h"

#ifndef _WIN32
#include <unistd.h>
#include <stdbool.h>
#include <sys/time.h>
#include <sys/utsname.h>
#else
#define alloca _alloca
#include <windows.h>
#define snprintf _snprintf
#endif

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>



#define  LUA_EXT_NAME  "encbtl"

#if LUA_VERSION_NUM == 501

#define lua_rawlen lua_objlen
#define luaL_newlib(L ,reg) luaL_register(L, LUA_EXT_NAME, reg)
#define luaL_buffinit(L , _ ) 
#define luaL_prepbuffsize( b , cap ) malloc(cap)
#define _Free(p) free(p)
#undef luaL_addsize
#define luaL_addsize(b , len) lua_pushlstring(L, temp , len) ; free(temp)
#define luaL_pushresult(b) 
#define luaL_checkversion(L)

#else

#define _Free(p)

#endif

#define LUCKYNUM              3396
#define BADNUM                14451
#define TOKEN_MAXLEN          1024
#define MAGIC_FACTORA         26762

static int _gen_token(lua_State *L)
{
	uint32_t uSessid = luaL_optinteger(L, 1, 0);
	uint32_t uHexp = luaL_optinteger(L, 2, 0);
    uint32_t uLval  = luaL_optinteger(L, 3, 0);
    uint32_t uBtlres  = luaL_optinteger(L, 4, 0);
    const char *secret = luaL_checkstring(L, 5);
    
    char tA[256] = {0}; 
    uint32_t seedA = (1000 + uSessid) * MAGIC_FACTORA;
    crandom_rmt_seed(seedA);
    uint32_t valA = crandom_rmt_rand();
    snprintf(tA, sizeof(tA), "%u", valA);

    char tB[256] = {0};
    crandom_rmt_seed(uHexp + uLval);
    uint32_t valB = crandom_rmt_rand();
    valB ^=  (valB << 15) & 0xefc65391;
    snprintf(tB, sizeof(tB), "%u", valB);

    char tC[256] = {0};
    if (uBtlres > 0) {
        //win
        snprintf(tC, sizeof(tC), "%u", LUCKYNUM);
    } else {
        snprintf(tC, sizeof(tC), "%u", BADNUM);
    }
    char token[256] = {0};
	snprintf(token, sizeof(token), "%s.%s.%s", tA, tB, tC);
    //加密token
	xxtea_long outlen = 0;
	const unsigned char *enc_token = xxtea_encrypt((unsigned char *)token
													, (xxtea_long)strlen(token)
													, (unsigned char *)secret
													, (xxtea_long)strlen(secret)
													, &outlen);

	lua_pushlstring(L, enc_token, outlen);
    return 1;
}

extern int luaopen_encbtl(lua_State *L) 
{
	luaL_Reg reg[] = 
	{
		{ "gentoken", _gen_token },

		{NULL,NULL},
	};

	luaL_checkversion(L);
	luaL_newlib(L, reg);

	return 1;
}
