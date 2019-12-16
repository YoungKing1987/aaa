package org.cocos2dx.lua;
import org.cocos2dx.lua.AppActivity;
import org.cocos2dx.lib.Cocos2dxLuaJavaBridge;
import org.cocos2dx.lib.Cocos2dxHelper;
public class LuaPlatform
{
    private static AppActivity sActivity = null;
    private static LuaPlatform sharedInstance_;
    static public LuaPlatform shareInstance(){
        if (sharedInstance_ == null)
            sharedInstance_ = new LuaPlatform();
        return sharedInstance_;
    }

    private  LuaPlatform(){
    }
    public static void init(final AppActivity activity)
    {
        sActivity = activity;
    }

    public static void initSdk(final String url,final String ass){
        sActivity.initSdk(url,ass);
    }

    public static void setLuaCallBack(final int lua){
        sActivity.setLuaCallBack(lua);
    }

    public static void getUserInfo (final  String userid){sActivity.getUserInfo(userid);}

    public static  void CheckPayOrder(){sActivity.CheckPayOrder();}

    public static  void gameserver_list(final String areaid){sActivity.gameserver_list(areaid);}

    public static  void pay(final String josn ){sActivity.pay(josn);}

    public static  void gameserver_select(final String sid){sActivity.gameserver_select(sid);}

    public static  void bindPlatform(final String platform){sActivity.bindPlatform(platform);}

    public static  void lianxi(final String uid){sActivity.lianxi(uid);}

    public static  void CountlyInfo(final String userid,final  String serverid,final  String sources,final  String appver,final  String userLevel){sActivity.CountlyInfo(userid,serverid,sources,appver, userLevel);}

    public static  void recordEvent(final String key){sActivity.recordEvent(key);}

    public static  void recordNewTaskEvent(final String key){sActivity.recordNewTaskEvent(key);}

//    public static void callbackLua(final String tipInfo,final int luaFunc){
//        Cocos2dxLuaJavaBridge.callLuaFunctionWithString(luaFunc, tipInfo);
//        Cocos2dxLuaJavaBridge.releaseLuaFunction(luaFunc);
//    }
}
