#ifndef __CCGH_H__
#define __CCGH_H__

#include "cocos2d.h"

class CCGh : public cocos2d::Ref
{
	typedef int LUA_STRING;

public:
	CCGh();
	~CCGh();

	// 获取lua代码设备根目录 
	static const char* GetLuaDeviceRoot();

	// 获取文件内容 
	static std::string GetFileData(const char* fileName);

	// 重新开始游戏 
	static void RestartGame();

	static void UnScheduleAll();

	static int IsRelease();

	static bool CallRenderRender();
};

#endif

