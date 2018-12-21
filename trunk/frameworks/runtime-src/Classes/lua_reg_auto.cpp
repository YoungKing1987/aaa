#include "lua_reg_auto.hpp"
#include "lua_extensions.h"
#include "scripting/lua-bindings/manual/LuaBasicConversions.h"
#include "scripting/lua-bindings/manual/tolua_fix.h"
#include "pbc-lua.h"

#include "gamehelp/CCGh.h"
#include "Log/LogMore.h"




int lua_game_LogMore_isInShowLog(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 0)
	{
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_isInShowLog'", nullptr);
			return 0;
		}
		bool ret = LogMore::isInShowLog();
		tolua_pushboolean(tolua_S, (bool)ret);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:isInShowLog", argc, 0);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_isInShowLog'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_logError(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 1)
	{
		const char* arg0;
		std::string arg0_tmp; ok &= luaval_to_std_string(tolua_S, 2, &arg0_tmp, "LogMore:logError"); arg0 = arg0_tmp.c_str();
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_logError'", nullptr);
			return 0;
		}
		LogMore::logError(arg0);
		lua_settop(tolua_S, 1);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:logError", argc, 1);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_logError'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_writeFileData(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 3)
	{
		const char* arg0;
		const char* arg1;
		const char* arg2;
		std::string arg0_tmp; ok &= luaval_to_std_string(tolua_S, 2, &arg0_tmp, "LogMore:writeFileData"); arg0 = arg0_tmp.c_str();
		std::string arg1_tmp; ok &= luaval_to_std_string(tolua_S, 3, &arg1_tmp, "LogMore:writeFileData"); arg1 = arg1_tmp.c_str();
		std::string arg2_tmp; ok &= luaval_to_std_string(tolua_S, 4, &arg2_tmp, "LogMore:writeFileData"); arg2 = arg2_tmp.c_str();
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_writeFileData'", nullptr);
			return 0;
		}
		bool ret = LogMore::writeFileData(arg0, arg1, arg2);
		tolua_pushboolean(tolua_S, (bool)ret);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:writeFileData", argc, 3);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_writeFileData'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_setOpenPrintLogModuleList(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 1)
	{
		std::vector<std::string> arg0;
		ok &= luaval_to_std_vector_string(tolua_S, 2, &arg0, "LogMore:setOpenPrintLogModuleList");
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_setOpenPrintLogModuleList'", nullptr);
			return 0;
		}
		LogMore::setOpenPrintLogModuleList(arg0);
		lua_settop(tolua_S, 1);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:setOpenPrintLogModuleList", argc, 1);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_setOpenPrintLogModuleList'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_pvpStart(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 0)
	{
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_pvpStart'", nullptr);
			return 0;
		}
		LogMore::pvpStart();
		lua_settop(tolua_S, 1);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:pvpStart", argc, 0);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_pvpStart'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_setLogLevel(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 1)
	{
		int arg0;
		ok &= luaval_to_int32(tolua_S, 2, (int *)&arg0, "LogMore:setLogLevel");
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_setLogLevel'", nullptr);
			return 0;
		}
		LogMore::setLogLevel(arg0);
		lua_settop(tolua_S, 1);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:setLogLevel", argc, 1);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_setLogLevel'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_printLogInfo(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 2)
	{
		std::string arg0;
		const char* arg1;
		ok &= luaval_to_std_string(tolua_S, 2, &arg0, "LogMore:printLogInfo");
		std::string arg1_tmp; ok &= luaval_to_std_string(tolua_S, 3, &arg1_tmp, "LogMore:printLogInfo"); arg1 = arg1_tmp.c_str();
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_printLogInfo'", nullptr);
			return 0;
		}
		LogMore::printLogInfo(arg0, arg1);
		lua_settop(tolua_S, 1);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:printLogInfo", argc, 2);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_printLogInfo'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_writeToFile(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 1)
	{
		const char* arg0;
		std::string arg0_tmp; ok &= luaval_to_std_string(tolua_S, 2, &arg0_tmp, "LogMore:writeToFile"); arg0 = arg0_tmp.c_str();
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_writeToFile'", nullptr);
			return 0;
		}
		bool ret = LogMore::writeToFile(arg0);
		tolua_pushboolean(tolua_S, (bool)ret);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:writeToFile", argc, 1);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_writeToFile'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_writeAllRecordToFile(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 0)
	{
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_writeAllRecordToFile'", nullptr);
			return 0;
		}
		LogMore::writeAllRecordToFile();
		lua_settop(tolua_S, 1);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:writeAllRecordToFile", argc, 0);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_writeAllRecordToFile'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_showErrorWindow(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 0)
	{
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_showErrorWindow'", nullptr);
			return 0;
		}
		LogMore::showErrorWindow();
		lua_settop(tolua_S, 1);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:showErrorWindow", argc, 0);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_showErrorWindow'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_init(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 0)
	{
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_init'", nullptr);
			return 0;
		}
		LogMore::init();
		lua_settop(tolua_S, 1);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:init", argc, 0);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_init'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_setNeedPrintLogModuleList(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 1)
	{
		std::vector<std::string> arg0;
		ok &= luaval_to_std_vector_string(tolua_S, 2, &arg0, "LogMore:setNeedPrintLogModuleList");
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_setNeedPrintLogModuleList'", nullptr);
			return 0;
		}
		LogMore::setNeedPrintLogModuleList(arg0);
		lua_settop(tolua_S, 1);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:setNeedPrintLogModuleList", argc, 1);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_setNeedPrintLogModuleList'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_writeRecordToFile(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 0)
	{
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_writeRecordToFile'", nullptr);
			return 0;
		}
		bool ret = LogMore::writeRecordToFile();
		tolua_pushboolean(tolua_S, (bool)ret);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:writeRecordToFile", argc, 0);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_writeRecordToFile'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_setIsPvp(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 1)
	{
		bool arg0;
		ok &= luaval_to_boolean(tolua_S, 2, &arg0, "LogMore:setIsPvp");
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_setIsPvp'", nullptr);
			return 0;
		}
		LogMore::setIsPvp(arg0);
		lua_settop(tolua_S, 1);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:setIsPvp", argc, 1);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_setIsPvp'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_insertOpenLogModule(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 1)
	{
		std::string arg0;
		ok &= luaval_to_std_string(tolua_S, 2, &arg0, "LogMore:insertOpenLogModule");
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_insertOpenLogModule'", nullptr);
			return 0;
		}
		LogMore::insertOpenLogModule(arg0);
		lua_settop(tolua_S, 1);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:insertOpenLogModule", argc, 1);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_insertOpenLogModule'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_pvpStop(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 0)
	{
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_pvpStop'", nullptr);
			return 0;
		}
		LogMore::pvpStop();
		lua_settop(tolua_S, 1);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:pvpStop", argc, 0);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_pvpStop'.", &tolua_err);
#endif
	return 0;
}
int lua_game_LogMore_insertNeeLogModule(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "LogMore", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 1)
	{
		std::string arg0;
		ok &= luaval_to_std_string(tolua_S, 2, &arg0, "LogMore:insertNeeLogModule");
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_LogMore_insertNeeLogModule'", nullptr);
			return 0;
		}
		LogMore::insertNeeLogModule(arg0);
		lua_settop(tolua_S, 1);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "LogMore:insertNeeLogModule", argc, 1);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_LogMore_insertNeeLogModule'.", &tolua_err);
#endif
	return 0;
}
static int lua_game_LogMore_finalize(lua_State* tolua_S)
{
	printf("luabindings: finalizing LUA object (LogMore)");
	return 0;
}
int lua_register_game_LogMore(lua_State* tolua_S)
{
	tolua_usertype(tolua_S, "LogMore");
	tolua_cclass(tolua_S, "LogMore", "LogMore", "", nullptr);

	tolua_beginmodule(tolua_S, "LogMore");
	tolua_function(tolua_S, "isInShowLog", lua_game_LogMore_isInShowLog);
	tolua_function(tolua_S, "logError", lua_game_LogMore_logError);
	tolua_function(tolua_S, "writeFileData", lua_game_LogMore_writeFileData);
	tolua_function(tolua_S, "setOpenPrintLogModuleList", lua_game_LogMore_setOpenPrintLogModuleList);
	tolua_function(tolua_S, "pvpStart", lua_game_LogMore_pvpStart);
	tolua_function(tolua_S, "setLogLevel", lua_game_LogMore_setLogLevel);
	tolua_function(tolua_S, "printLogInfo", lua_game_LogMore_printLogInfo);
	tolua_function(tolua_S, "writeToFile", lua_game_LogMore_writeToFile);
	tolua_function(tolua_S, "writeAllRecordToFile", lua_game_LogMore_writeAllRecordToFile);
	tolua_function(tolua_S, "showErrorWindow", lua_game_LogMore_showErrorWindow);
	tolua_function(tolua_S, "init", lua_game_LogMore_init);
	tolua_function(tolua_S, "setNeedPrintLogModuleList", lua_game_LogMore_setNeedPrintLogModuleList);
	tolua_function(tolua_S, "writeRecordToFile", lua_game_LogMore_writeRecordToFile);
	tolua_function(tolua_S, "setIsPvp", lua_game_LogMore_setIsPvp);
	tolua_function(tolua_S, "insertOpenLogModule", lua_game_LogMore_insertOpenLogModule);
	tolua_function(tolua_S, "pvpStop", lua_game_LogMore_pvpStop);
	tolua_function(tolua_S, "insertNeeLogModule", lua_game_LogMore_insertNeeLogModule);
	tolua_endmodule(tolua_S);
	std::string typeName = typeid(LogMore).name();
	g_luaType[typeName] = "LogMore";
	g_typeCast["LogMore"] = "LogMore";
	return 1;
}

int lua_game_CCGh_CallRenderRender(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "CCGh", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 0)
	{
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_CCGh_CallRenderRender'", nullptr);
			return 0;
		}
		bool ret = CCGh::CallRenderRender();
		tolua_pushboolean(tolua_S, (bool)ret);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "CCGh:CallRenderRender", argc, 0);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_CCGh_CallRenderRender'.", &tolua_err);
#endif
	return 0;
}
int lua_game_CCGh_GetLuaDeviceRoot(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "CCGh", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 0)
	{
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_CCGh_GetLuaDeviceRoot'", nullptr);
			return 0;
		}
		const char* ret = CCGh::GetLuaDeviceRoot();
		tolua_pushstring(tolua_S, (const char*)ret);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "CCGh:GetLuaDeviceRoot", argc, 0);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_CCGh_GetLuaDeviceRoot'.", &tolua_err);
#endif
	return 0;
}
int lua_game_CCGh_GetFileData(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "CCGh", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 1)
	{
		const char* arg0;
		std::string arg0_tmp; ok &= luaval_to_std_string(tolua_S, 2, &arg0_tmp, "CCGh:GetFileData"); arg0 = arg0_tmp.c_str();
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_CCGh_GetFileData'", nullptr);
			return 0;
		}
		std::string ret = CCGh::GetFileData(arg0);
		tolua_pushcppstring(tolua_S, ret);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "CCGh:GetFileData", argc, 1);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_CCGh_GetFileData'.", &tolua_err);
#endif
	return 0;
}
int lua_game_CCGh_RestartGame(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "CCGh", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 0)
	{
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_CCGh_RestartGame'", nullptr);
			return 0;
		}
		CCGh::RestartGame();
		lua_settop(tolua_S, 1);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "CCGh:RestartGame", argc, 0);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_CCGh_RestartGame'.", &tolua_err);
#endif
	return 0;
}
int lua_game_CCGh_IsRelease(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "CCGh", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 0)
	{
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_CCGh_IsRelease'", nullptr);
			return 0;
		}
		int ret = CCGh::IsRelease();
		tolua_pushnumber(tolua_S, (lua_Number)ret);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "CCGh:IsRelease", argc, 0);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_CCGh_IsRelease'.", &tolua_err);
#endif
	return 0;
}
int lua_game_CCGh_UnScheduleAll(lua_State* tolua_S)
{
	int argc = 0;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif

#if COCOS2D_DEBUG >= 1
	if (!tolua_isusertable(tolua_S, 1, "CCGh", 0, &tolua_err)) goto tolua_lerror;
#endif

	argc = lua_gettop(tolua_S) - 1;

	if (argc == 0)
	{
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_CCGh_UnScheduleAll'", nullptr);
			return 0;
		}
		CCGh::UnScheduleAll();
		lua_settop(tolua_S, 1);
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n ", "CCGh:UnScheduleAll", argc, 0);
	return 0;
#if COCOS2D_DEBUG >= 1
tolua_lerror:
	tolua_error(tolua_S, "#ferror in function 'lua_game_CCGh_UnScheduleAll'.", &tolua_err);
#endif
	return 0;
}
int lua_game_CCGh_constructor(lua_State* tolua_S)
{
	int argc = 0;
	CCGh* cobj = nullptr;
	bool ok = true;

#if COCOS2D_DEBUG >= 1
	tolua_Error tolua_err;
#endif



	argc = lua_gettop(tolua_S) - 1;
	if (argc == 0)
	{
		if (!ok)
		{
			tolua_error(tolua_S, "invalid arguments in function 'lua_game_CCGh_constructor'", nullptr);
			return 0;
		}
		cobj = new CCGh();
		cobj->autorelease();
		int ID = (int)cobj->_ID;
		int* luaID = &cobj->_luaID;
		toluafix_pushusertype_ccobject(tolua_S, ID, luaID, (void*)cobj, "CCGh");
		return 1;
	}
	luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d \n", "CCGh:CCGh", argc, 0);
	return 0;

#if COCOS2D_DEBUG >= 1
	tolua_error(tolua_S, "#ferror in function 'lua_game_CCGh_constructor'.", &tolua_err);
#endif

	return 0;
}
static int lua_game_CCGh_finalize(lua_State* tolua_S)
{
	printf("luabindings: finalizing LUA object (CCGh)");
	return 0;
}
int lua_register_game_CCGh(lua_State* tolua_S)
{
	tolua_usertype(tolua_S, "CCGh");
	tolua_cclass(tolua_S, "CCGh", "CCGh", "cc.Ref", nullptr);

	tolua_beginmodule(tolua_S, "CCGh");
	tolua_function(tolua_S, "new", lua_game_CCGh_constructor);
	tolua_function(tolua_S, "CallRenderRender", lua_game_CCGh_CallRenderRender);
	tolua_function(tolua_S, "GetLuaDeviceRoot", lua_game_CCGh_GetLuaDeviceRoot);
	tolua_function(tolua_S, "GetFileData", lua_game_CCGh_GetFileData);
	tolua_function(tolua_S, "RestartGame", lua_game_CCGh_RestartGame);
	tolua_function(tolua_S, "IsRelease", lua_game_CCGh_IsRelease);
	tolua_function(tolua_S, "UnScheduleAll", lua_game_CCGh_UnScheduleAll);
	tolua_endmodule(tolua_S);
	std::string typeName = typeid(CCGh).name();
	g_luaType[typeName] = "CCGh";
	g_typeCast["CCGh"] = "CCGh";
	return 1;
}

TOLUA_API int register_all(lua_State* tolua_S)
{
	// 注册库
	luaopen_protobufc(tolua_S);
	luaopen_lua_extensions_ex(tolua_S);
	//luaopen_lua_extra(tolua_S);

	// 注册自定义类
	tolua_open(tolua_S);
	tolua_module(tolua_S,nullptr,0);
	tolua_beginmodule(tolua_S,nullptr);

	lua_register_game_LogMore(tolua_S);

	lua_register_game_CCGh(tolua_S);

	tolua_endmodule(tolua_S);
	return 1;
}

