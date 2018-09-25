#ifndef __CCGH_H__
#define __CCGH_H__

#include "cocos2d.h"

class CCGh : public cocos2d::Ref
{
	typedef int LUA_STRING;

public:
	CCGh();
	~CCGh();

	// ��ȡlua�����豸��Ŀ¼ 
	static const char* GetLuaDeviceRoot();

	// ��ȡ�ļ����� 
	static std::string GetFileData(const char* fileName);

	// ���¿�ʼ��Ϸ 
	static void RestartGame();

	static void UnScheduleAll();

	static int IsRelease();

	static bool CallRenderRender();
};

#endif

