
#include "cocos2d.h"

#if CC_TARGET_PLATFORM == CC_PLATFORM_WIN32

#include "native/CCNative.h"
#include "platform/win32/CCNativeWin32.h"

#include <windows.h> 
#include <wincon.h> 
#include <stdio.h> 
#include <nb30.h>
#include <string>

NS_CC_EXTRA_BEGIN

void CCNative::showActivityIndicator(void)
{
	CCNativeWin32::sharedInstance()->showActivityIndicator();
}

void CCNative::hideActivityIndicator(void)
{
	CCNativeWin32::sharedInstance()->hideActivityIndicator();
}


void CCNative::createAlert(const char* title,
                           const char* message,
                           const char* cancelButtonTitle)
{
	CCNativeWin32::sharedInstance()->createAlertView(title, message, cancelButtonTitle);
}

int CCNative::addAlertButton(const char* buttonTitle)
{
	return CCNativeWin32::sharedInstance()->addAlertButton(buttonTitle);
}

#if CC_LUA_ENGINE_ENABLED > 0
int CCNative::addAlertButtonLua(const char* buttonTitle)
{
    return addAlertButton(buttonTitle) + 1;
}
#endif

void CCNative::showAlert(CCAlertViewDelegate* delegate)
{
	CCNativeWin32::sharedInstance()->showAlertViewWithDelegate(delegate);
}

#if CC_LUA_ENGINE_ENABLED > 0
void CCNative::showAlertLua(cocos2d::LUA_FUNCTION listener)
{
	CCNativeWin32::sharedInstance()->showAlertViewWithLuaListener(listener);
}
#endif

void CCNative::cancelAlert(void)
{
	CCNativeWin32::sharedInstance()->cancelAlertView();
}

std::string getOneWin32Mac(int lana_num)
{
// 	const int MACSESION = 6;
// 
// 	typedef struct _ASTAT_
// 	{
// 		ADAPTER_STATUS adapt;
// 		NAME_BUFFER    NameBuff[30];
// 	}ASTAT, *PASTAT;
// 	ASTAT Adapter;
// 
// 	NCB ncb;
// 	UCHAR uRetCode;
// 
// 	memset(&ncb, 0, sizeof(ncb));
// 	ncb.ncb_command = NCBRESET;
// 	ncb.ncb_lana_num = lana_num;
// 
// 	uRetCode = Netbios(&ncb);
// 	//printf( "The NCBRESET return code is:0x%x \n", uRetCode ); 
// 
// 	memset(&ncb, 0, sizeof(ncb));
// 	ncb.ncb_command = NCBASTAT;
// 	ncb.ncb_lana_num = lana_num;
// 
// 	strcpy((char *)ncb.ncb_callname, "* ");
// 	ncb.ncb_buffer = (unsigned char *)&Adapter;
// 
// 	ncb.ncb_length = sizeof(Adapter);
// 	uRetCode = Netbios(&ncb);
	// printf( "The NCBASTAT return code is: 0x%x \n", uRetCode );
	std::string s;
// 	if (uRetCode == 0)
// 	{
// 		int bAddressInt[MACSESION];
// 		char CommarSeperatedAddress[MACSESION * 3] = { 0 };
// 		for (int i = 0; i < MACSESION; ++i)
// 		{
// 			bAddressInt[i] = Adapter.adapt.adapter_address[i];
// 			bAddressInt[i] &= 0x000000ff; // avoid "ff" leading bytes when "char" is lager then 0x7f
// 		}
// 		sprintf(CommarSeperatedAddress, "%02X:%02X:%02X:%02X:%02X:%02X",
// 			bAddressInt[0],
// 			bAddressInt[1],
// 			bAddressInt[2],
// 			bAddressInt[3],
// 			bAddressInt[4],
// 			bAddressInt[5]); // Should use scl::FormatString inside 
// 		s = CommarSeperatedAddress;
// 	}
	return s;
}

const std::string CCNative::getOpenUDID(void)
{
	typedef std::vector< std::string > MACAddresses;
	typedef MACAddresses::iterator vsIt;

// 	NCB ncb;
// 	UCHAR uRetCode;
// 	LANA_ENUM lana_enum;
// 	memset(&ncb, 0, sizeof(ncb));
// 	ncb.ncb_command = NCBENUM;
// 
// 	ncb.ncb_buffer = (unsigned char *)&lana_enum;
// 	ncb.ncb_length = sizeof(lana_enum);
// 
// 	uRetCode = Netbios(&ncb);
// 	//printf( "The NCBENUM return code is:0x%x \n", uRetCode );
// 	MACAddresses vAdd;
// 	if (uRetCode == 0)
// 	{
// 		//printf( "Ethernet Count is : %d\n\n", lana_enum.length); 
// 		for (int i = 0; i < lana_enum.length; ++i)
// 		{
// 			std::string s = getOneWin32Mac(lana_enum.lana[i]);
// 			if (!s.empty())
// 			{
// 				vAdd.push_back("WIN:" + s);
// 			}
// 		}
// 	}
// 
// 	if (vAdd.size() > 0)
// 	{
// 		return vAdd.front();
// 	}
// 	
	return "";
}

void CCNative::openURL(const char* url)
{
    if (!url) return;
}

const std::string CCNative::getInputText(const char* title, const char* message, const char* defaultValue)
{
	return CCNativeWin32::sharedInstance()->getInputText(title, message, defaultValue);
}

const string CCNative::getDeviceName(void)
{
    return "Win32";
}

void CCNative::vibrate()
{
}


NS_CC_EXTRA_END

#endif