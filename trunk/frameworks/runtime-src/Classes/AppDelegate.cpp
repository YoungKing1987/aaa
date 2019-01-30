#include "AppDelegate.h"
#include "scripting/lua-bindings/manual/CCLuaEngine.h"
//#include "audio/include/SimpleAudioEngine.h"
#include "cocos2d.h"
#include "scripting/lua-bindings/manual/lua_module_register.h"

#include "lua_reg_auto.hpp"

 #define USE_AUDIO_ENGINE 1
// #define USE_SIMPLE_AUDIO_ENGINE 1

#if USE_AUDIO_ENGINE && USE_SIMPLE_AUDIO_ENGINE
#error "Don't use AudioEngine and SimpleAudioEngine at the same time. Please just select one in your game!"
#endif

#if USE_AUDIO_ENGINE
#include "audio/include/AudioEngine.h"
using namespace cocos2d::experimental;
#elif USE_SIMPLE_AUDIO_ENGINE
#include "audio/include/SimpleAudioEngine.h"
using namespace CocosDenshion;
#endif
USING_NS_CC;
using namespace std;


extern "C"
{
	int hgame_lua_loader(lua_State *L)
	{
		std::string filename(luaL_checkstring(L, 1));
		size_t pos = filename.rfind(".lua");
		if (pos != std::string::npos)
		{
			filename = filename.substr(0, pos);
		}

		pos = filename.find_first_of(".");
		while (pos != std::string::npos)
		{
			filename.replace(pos, 1, "/");
			pos = filename.find_first_of(".");
		}
		filename.append(".lua");

		ssize_t codeBufferSize = 0;
		std::string fullPath = FileUtils::getInstance()->fullPathForFilename(filename.c_str());
		unsigned char* codeBuffer = FileUtils::getInstance()->getFileData(fullPath.c_str(), "rb", &codeBufferSize);

		if (codeBuffer)
		{

			//CONFIG_DECRPYT(codeBuffer, codeBufferSize);

			if (luaL_loadbuffer(L, (char*)codeBuffer, codeBufferSize, filename.c_str()) != 0)
			{
				luaL_error(L, "error loading module %s from file %s :\n\t%s",
					lua_tostring(L, 1), filename.c_str(), lua_tostring(L, -1));
			}
			else
			{
				//CCLOG("hgame_lua_loader %s", fullPath.c_str());
			}
			delete[] codeBuffer;
		}
		else
		{
			CCLOG("can not get file data of %s", filename.c_str());
		}

		return 1;
	}
}

static AppDelegate* instance = NULL;
static bool isSystemInited = false;

class DelayCallBack : public cocos2d::Ref
{
public:
	void delayCallback(float dt)
	{
		if (!isSystemInited)
		{
			instance->initSystem();
			isSystemInited = true;
		}

		Director::getInstance()->getScheduler()->unscheduleAllForTarget(this);

		release();
	}
};


AppDelegate::AppDelegate()
{
	instance = this;
}

AppDelegate::~AppDelegate()
{
#if USE_AUDIO_ENGINE
	AudioEngine::end();
#elif USE_SIMPLE_AUDIO_ENGINE
	SimpleAudioEngine::end();
#endif

#if (COCOS2D_DEBUG > 0) && (CC_CODE_IDE_DEBUG_SUPPORT > 0)
    // NOTE:Please don't remove this call if you want to debug with Cocos Code IDE
    RuntimeEngine::getInstance()->end();
#endif

}

// if you want a different context, modify the value of glContextAttrs
// it will affect all platforms
void AppDelegate::initGLContextAttrs()
{
    // set OpenGL context attributes: red,green,blue,alpha,depth,stencil
    GLContextAttrs glContextAttrs = {8, 8, 8, 8, 24, 8};

    GLView::setGLContextAttrs(glContextAttrs);
}

// if you want to use the package manager to install more packages, 
// don't modify or remove this function
static int register_all_packages()
{
    return 0; //flag for packages manager
}

bool AppDelegate::applicationDidFinishLaunching()
{
    // set default FPS
	/*
    Director::getInstance()->setAnimationInterval(1.0 / 60.0f);

	Scene* s = Scene::create();

	Director::getInstance()->runWithScene(s);

	s->addChild(LayerColor::create(Color4B(255, 255, 255, 255)));
	DelayCallBack* obj = new DelayCallBack;
	Director::getInstance()->getScheduler()->schedule(
		schedule_selector(DelayCallBack::delayCallback), obj, 0.08, 0, 0.08, false);
	*/
	DelayCallBack* obj = new DelayCallBack;
	obj->delayCallback(0.0);
	
    return true;
}

// This function will be called when the app is inactive. Note, when receiving a phone call it is invoked.
void AppDelegate::applicationDidEnterBackground()
{
	if (!isSystemInited)
	{
		return;
	}

	Director::getInstance()->stopAnimation();
	Director::getInstance()->pause();

#if USE_AUDIO_ENGINE
	AudioEngine::pauseAll();
#elif USE_SIMPLE_AUDIO_ENGINE
	SimpleAudioEngine::getInstance()->pauseBackgroundMusic();
	SimpleAudioEngine::getInstance()->pauseAllEffects();
#endif

	auto engine = LuaEngine::getInstance();
	ScriptEngineManager::getInstance()->setScriptEngine(engine);

	LuaStack* pStack = engine->getLuaStack();
	lua_State* pLuaSt = pStack->getLuaState();
	lua_getglobal(pLuaSt, "gevent_onpause");
	lua_pcall(pLuaSt, 0, 0, 0);
}

// this function will be called when the app is active again
void AppDelegate::applicationWillEnterForeground()
{
	if (!isSystemInited)
	{
		return;
	}

	Director::getInstance()->startAnimation();
	Director::getInstance()->resume();

#if USE_AUDIO_ENGINE
	AudioEngine::resumeAll();
#elif USE_SIMPLE_AUDIO_ENGINE
	SimpleAudioEngine::getInstance()->resumeBackgroundMusic();
	SimpleAudioEngine::getInstance()->resumeAllEffects();
#endif

	auto engine = LuaEngine::getInstance();
	ScriptEngineManager::getInstance()->setScriptEngine(engine);

	LuaStack* pStack = engine->getLuaStack();
	lua_State* pLuaSt = pStack->getLuaState();
	lua_getglobal(pLuaSt, "gevent_onresume");
	lua_pcall(pLuaSt, 0, 0, 0);
}


bool AppDelegate::initGame(void)
{

	// register lua module
	auto engine = LuaEngine::getInstance();
	ScriptEngineManager::getInstance()->setScriptEngine(engine);
	LuaStack* stack = engine->getLuaStack();
	stack->setXXTEAKeyAndSign("WangBingSheng", strlen("WangBingSheng"), "ShengBingWang", strlen("ShengBingWang"));
	lua_State* L = stack->getLuaState();

	lua_module_register(L);

	register_all_packages();

	register_all(L);


	//д���ȸ���Ŀ¼��lua�����ļ������ȸ�
	/*std::string strPath = FileUtils::getInstance()->getWritablePath();
	FileUtils::getInstance()->addSearchPath(strPath + "patch/src", true);
	FileUtils::getInstance()->addSearchPath(strPath + "patch/res", true);*/

#if CC_64BITS
	FileUtils::getInstance()->addSearchPath("src/64bit");
#endif
	string storagePath = FileUtils::getInstance()->getWritablePath() + "patch/";
	string path1 = storagePath + "res";
	string path2 = storagePath + "res/ui";
	string path3 = storagePath + "src";
	if (!FileUtils::getInstance()->isDirectoryExist(storagePath))
	{
		FileUtils::getInstance()->createDirectory(storagePath);
		FileUtils::getInstance()->createDirectory(path1);
		FileUtils::getInstance()->createDirectory(path2);
		FileUtils::getInstance()->createDirectory(path3);
	}
	FileUtils::getInstance()->addSearchPath(path1);
	FileUtils::getInstance()->addSearchPath(path2);
	FileUtils::getInstance()->addSearchPath(path3);
	FileUtils::getInstance()->addSearchPath("res");
	FileUtils::getInstance()->addSearchPath("res/ui");
	FileUtils::getInstance()->addSearchPath("src");
	if (engine->executeScriptFile("start.lua"))
    {
        return false;
    }
	//pEngine->executeScriptFile("main.lua");
	//Director::getInstance()->getKeypadDispatcher()->addDelegate(this);

	return true;
}


bool AppDelegate::initScriptEngine(void)
{

	// register lua module
	auto engine = LuaEngine::getInstance();
	ScriptEngineManager::getInstance()->setScriptEngine(engine);

	LuaStack* pStack = engine->getLuaStack();

	lua_State* pLuaSt = pStack->getLuaState();

	lua_module_register(pLuaSt);

	pStack->addLuaLoader(hgame_lua_loader);

	return true;
}


void AppDelegate::initSystem(void)
{
	//initScriptEngine();
	initGame();
}

void AppDelegate::applicationRestart()
{
#if USE_AUDIO_ENGINE
	AudioEngine::stopAll();
#elif USE_SIMPLE_AUDIO_ENGINE
	SimpleAudioEngine::getInstance()->stopAllEffects();
	SimpleAudioEngine::getInstance()->stopBackgroundMusic(true);
#endif
	

	Director* director = Director::getInstance();

	//cocostudio::DataReaderHelper::getInstance()->clear();

	director->getScheduler()->scheduleUpdate(director->getActionManager(), Scheduler::PRIORITY_SYSTEM, false);

	director->purgeCachedData();

	ScriptEngineManager::getInstance()->removeScriptEngine();

	initSystem();
}