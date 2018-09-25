
#ifndef __COCOS2D_X_EXTRA_H_
#define __COCOS2D_X_EXTRA_H_

#include "cocos2d.h"
#include <string>

using namespace std;
using namespace cocos2d;

#define CC_LUA_ENGINE_ENABLED  1

#define NS_CC_EXTRA_BEGIN namespace cocos2d { namespace extra {
#define NS_CC_EXTRA_END   }}
#define USING_NS_CC_EXTRA using namespace cocos2d::extra

#ifdef __cplusplus
extern "C" {
#endif

#include "lauxlib.h"

void  luaopen_lua_extra(lua_State *L);

#ifdef __cplusplus
}
#endif

#endif /* __COCOS2D_X_EXTRA_H_ */
