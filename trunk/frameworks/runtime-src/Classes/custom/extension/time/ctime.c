#ifdef __cplusplus
extern "C" {
#endif
#include "lua.h"
#include "lualib.h"
#include "lauxlib.h"
#ifdef __cplusplus
}
#endif

#ifndef _WIN32
#include <unistd.h>
#include <stdbool.h>
#include <sys/time.h>
#include <sys/utsname.h>
#else
#define alloca _alloca
#include <windows.h>
#endif

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>
#include <time.h>


#if LUA_VERSION_NUM == 501

#define lua_rawlen lua_objlen
#define luaL_newlib(L ,reg) luaL_register(L, "ctime", reg)
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

static int64_t _sys_time_in_us()
{
#ifdef _WIN32
	// Number of 100 nanosecond units from 1/1/1601 to 1/1/1970
	#define EPOCH_BIAS  116444736000000000i64

	ULARGE_INTEGER uli;
	GetSystemTimeAsFileTime((FILETIME*)(&uli));
	return (uli.QuadPart - EPOCH_BIAS) / 10;
#else
	struct timeval tv;
	gettimeofday(&tv, NULL);
	int64_t result = tv.tv_sec;
	result *= 1000000;
	result += tv.tv_usec;
	return result;
#endif
}

static int _time_clock(lua_State *L) 
{
	lua_pushnumber(L, (double)_sys_time_in_us() / 1000000.0f);
	return 1;
}

static int _time_now(lua_State *L)
{
	struct tm curr;
	int64_t  nowUsSec = _sys_time_in_us();
	double   nowSec   = (double)nowUsSec / 1000000.0f;
	time_t   nowTime  = (time_t)nowSec;
	
	curr = *localtime(&nowTime);

	lua_pushnumber(L, nowSec);
	lua_pushinteger(L, curr.tm_year + 1900);
	lua_pushinteger(L, curr.tm_mon + 1);
	lua_pushinteger(L, curr.tm_mday);
	lua_pushinteger(L, curr.tm_hour);
	lua_pushinteger(L, curr.tm_min);
	lua_pushinteger(L, curr.tm_sec);
	lua_pushinteger(L, curr.tm_wday);
	lua_pushinteger(L, curr.tm_yday);

	return 9;
}

static int _time_totm(lua_State *L)
{
	struct tm curr;
	time_t nowTime = luaL_optinteger(L, 1, 0);
	curr = *localtime(&nowTime);

	lua_pushnumber(L, nowTime);
	lua_pushinteger(L, curr.tm_year + 1900);
	lua_pushinteger(L, curr.tm_mon + 1);
	lua_pushinteger(L, curr.tm_mday);
	lua_pushinteger(L, curr.tm_hour);
	lua_pushinteger(L, curr.tm_min);
	lua_pushinteger(L, curr.tm_sec);
	lua_pushinteger(L, curr.tm_wday);
	lua_pushinteger(L, curr.tm_yday);

	return 9;
}

static int _time_togmtm(lua_State *L)
{
    struct tm curr;
    time_t nowTime = luaL_optinteger(L, 1, 0);
    curr = *gmtime(&nowTime);

    lua_pushnumber(L, nowTime);
    lua_pushinteger(L, curr.tm_year + 1900);
    lua_pushinteger(L, curr.tm_mon + 1);
    lua_pushinteger(L, curr.tm_mday);
    lua_pushinteger(L, curr.tm_hour);
    lua_pushinteger(L, curr.tm_min);
    lua_pushinteger(L, curr.tm_sec);
    lua_pushinteger(L, curr.tm_wday);
    lua_pushinteger(L, curr.tm_yday);

    return 9;
}

static int _time_sleep(lua_State *L)
{
	uint32_t iMsSec = luaL_optinteger(L, 1, 0);
#ifdef _WIN32
	Sleep(iMsSec);
#else
	usleep(iMsSec * 1000);
#endif
	return 0;
}

extern int luaopen_ctime(lua_State *L) 
{
	luaL_Reg reg[] = 
	{
		{ "clock", _time_clock },
		{ "now", _time_now },
		{ "sleep", _time_sleep },
        { "totm", _time_totm },
        { "togmtm", _time_togmtm },

		{NULL,NULL},
	};

	luaL_checkversion(L);
	luaL_newlib(L, reg);

	return 1;
}

