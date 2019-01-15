//
//  LuaObjectCBridge.h
//  MyLuaGame-mobile
//
//  Created by yj on 2019/1/10.
//

#ifndef LuaObjectCBridge_h
#define LuaObjectCBridge_h
#import <Foundation/Foundation.h>

@interface LuaObjectCBridge : NSObject{
    int _scriptHandler;
}
+ (LuaObjectCBridge*) getInstance;
+ (void) destroyInstance;

+ (void) registerScriptHandler:(NSDictionary *)dict;
+ (void) unregisterScriptHandler;
- (int) getScriptHandler;
- (void) setScriptHandler:(int)scriptHandler;
- (id) init;
@end

#endif /* LuaObjectCBridge_h */
