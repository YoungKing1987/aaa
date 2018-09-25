

#include <string>

//#include "CCLuaEngine.h"
#include "cocos2d.h"
#include "AppDelegate.h"

//#include "Lua/AxLuaFunction.h"

//#include "Proto/pbconf.client.loader.h"

#include "CCGh.h"

USING_NS_CC;

CCGh::CCGh()
{
}

CCGh::~CCGh()
{
}

const char* CCGh::GetLuaDeviceRoot()
{
	static std::string strLuaRootPath;
	if (strLuaRootPath != "")
	{
		return strLuaRootPath.c_str();
	}

	const std::string strLuaBootFile = "app_boot.lua";
	std::string strPath = FileUtils::getInstance()->fullPathForFilename(strLuaBootFile.c_str());
	std::string::size_type pos = strPath.rfind(strLuaBootFile);
	if (std::string::npos == pos)
	{
		CCAssert(false, "lua script root dir not found!");
	}

	strLuaRootPath = strPath.substr(0, --pos);
	return strLuaRootPath.c_str();
}

std::string CCGh::GetFileData(const char* fileName)
{
	ssize_t ulSize = 0;
	unsigned char* pBuf = FileUtils::getInstance()->getFileData(fileName, "rb", &ulSize);
	if (pBuf == NULL)
	{
		return NULL;
	}

	std::string contentStr((const char*)pBuf, ulSize);
	delete[] pBuf;
	return contentStr;
}

void CCGh::RestartGame()
{
	class ScriptEngineRemove : public Ref
	{
	public:
		static ScriptEngineRemove* create(void)
		{
			ScriptEngineRemove* pRet = new ScriptEngineRemove();
			if (pRet)
			{
				pRet->autorelease();
			}
			else
			{
				CC_SAFE_DELETE(pRet);
			}
			return pRet;
		}

		void callBack(float dt)
		{
			AppDelegate* pApp = (AppDelegate*)Application::getInstance();
			pApp->applicationRestart();
		}

	};

	Director* director = Director::getInstance();
	//director->getScheduler()->unscheduleAll();
	ScriptEngineRemove* pObj = ScriptEngineRemove::create();
	director->getScheduler()->schedule(schedule_selector(ScriptEngineRemove::callBack), pObj, 0, 0, 0.2f, false);
}

void CCGh::UnScheduleAll()
{
	Director* director = Director::getInstance();
	ActionManager* actionMgr = director->getActionManager();
	actionMgr->removeAllActions();
	director->getScheduler()->unscheduleAll();
	director->getScheduler()->scheduleUpdateForTarget(actionMgr, kCCPrioritySystem, false);
}

int CCGh::IsRelease()
{
	int ret = 0;
#ifdef NDEBUG
	ret = 1;
#endif
	return ret;
}

bool CCGh::CallRenderRender()
{
	Director::getInstance()->getRenderer()->render();
	return true;
}