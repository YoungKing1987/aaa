//
//  LuaObjectCBridge.m
//  MyLuaGame-mobile
//
//  Created by yj on 2019/1/10.
//

#import "LuaObjectCBridge.h"

#import "cocos2d.h"
#import "scripting/lua-bindings/manual/CCLuaEngine.h"
#import "scripting/lua-bindings/manual/CCLuaBridge.h"


using namespace cocos2d;

@implementation LuaObjectCBridge

static LuaObjectCBridge* s_instance = nil;

- (id)init
{
    _scriptHandler = 0;
    return self;
}

+ (LuaObjectCBridge*) getInstance
{
    if (!s_instance)
    {
        s_instance = [LuaObjectCBridge alloc];
        [s_instance init];
    }
    
    return s_instance;
}

+ (void) destroyInstance
{
    [s_instance release];
}

- (void) setScriptHandler:(int)scriptHandler
{
    if (_scriptHandler)
    {
        LuaBridge::releaseLuaFunctionById(_scriptHandler);
        _scriptHandler = 0;
    }
    _scriptHandler = scriptHandler;
}

- (int) getScriptHandler
{
    return _scriptHandler;
}


+(void) registerScriptHandler:(NSDictionary *)dict
{
    [[LuaObjectCBridge getInstance] setScriptHandler:[[dict objectForKey:@"scriptHandler"] intValue]];
}


+ (void) unregisterScriptHandler
{
    [[LuaObjectCBridge getInstance] setScriptHandler:0];
}

@end
