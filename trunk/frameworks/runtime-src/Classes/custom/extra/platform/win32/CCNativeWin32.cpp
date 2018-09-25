
#include "cocos2d.h"

#if CC_TARGET_PLATFORM == CC_PLATFORM_WIN32

#include "platform/win32/CCNativeWin32.h"
#include "platform/win32/CCNativeWin32def.h"
#include "platform/CCGLView.h"

CCNativeWin32* CCNativeWin32::s_sharedInstance = NULL;

CCNativeWin32* CCNativeWin32::sharedInstance(void)
{
	if (!s_sharedInstance)
    {
		s_sharedInstance = new CCNativeWin32();
    }
    return s_sharedInstance;
}

CCNativeWin32::CCNativeWin32(void)
{
}


void CCNativeWin32::showActivityIndicator(void)
{
}

void CCNativeWin32::hideActivityIndicator(void)
{
}


void CCNativeWin32::createAlertView(const char* title, const char *message, const char* cancelButtonTitle)
{
	m_alertViewTitle = string(title ? title : "");
	m_alertViewMessage = string(message ? message : "");
}

int CCNativeWin32::addAlertButton(const char* buttonTitle)
{
	return 0;
}

void CCNativeWin32::showAlertViewWithDelegate(CCAlertViewDelegate *delegate)
{
    MessageBox(m_alertViewMessage.c_str(), m_alertViewTitle.c_str());
    delegate->alertViewClickedButtonAtIndex(0);
}

void CCNativeWin32::removeAlertView(void)
{
}

void CCNativeWin32::cancelAlertView(void)
{
}

#if CC_LUA_ENGINE_ENABLED > 0
void CCNativeWin32::showAlertViewWithLuaListener(LUA_FUNCTION listener)
{
    MessageBox(m_alertViewMessage.c_str(), m_alertViewTitle.c_str());
    
    LuaValueDict event;
    event["action"] = LuaValue::stringValue("clicked");
    event["buttonIndex"] = LuaValue::intValue(1);

	LuaStack* stack = LuaEngine::getInstance()->getLuaStack();
	stack->pushLuaValueDict(event);
    stack->executeFunctionByHandler(listener, 1);
}

void CCNativeWin32::removeAlertViewLuaListener(void)
{
}
#endif

const string CCNativeWin32::getInputText(const char* title, const char* message, const char* defaultValue)
{
    return "empty";
}

#endif