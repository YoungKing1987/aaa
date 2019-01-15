/****************************************************************************
 Copyright (c) 2013      cocos2d-x.org
 Copyright (c) 2013-2016 Chukong Technologies Inc.
 Copyright (c) 2017-2018 Xiamen Yaji Software Co., Ltd.

 http://www.cocos2d-x.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ****************************************************************************/

#import "RootViewController.h"
#import "LuaObjectCBridge.h"
#import "cocos2d.h"
#import "platform/ios/CCEAGLView-ios.h"
#import "scripting/lua-bindings/manual/CCLuaEngine.h"
#import "scripting/lua-bindings/manual/CCLuaBridge.h"
#include "platform/CCFileUtils.h"


using namespace cocos2d;


@implementation RootViewController

/*
 // The designated initializer.  Override if you create the controller programmatically and want to perform customization that is not appropriate for viewDidLoad.
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    if ((self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil])) {
        // Custom initialization
    }
    return self;
}
*/

// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView {
    // Initialize the CCEAGLView
    CCEAGLView *eaglView = [CCEAGLView viewWithFrame: [UIScreen mainScreen].bounds
                                         pixelFormat: (__bridge NSString *)cocos2d::GLViewImpl::_pixelFormat
                                         depthFormat: cocos2d::GLViewImpl::_depthFormat
                                  preserveBackbuffer: NO
                                          sharegroup: nil
                                       multiSampling: NO
                                     numberOfSamples: 0 ];
    
    // Enable or disable multiple touches
    [eaglView setMultipleTouchEnabled:NO];
    
    // Set EAGLView as view of RootViewController
    self.view = eaglView;
}

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
}


// For ios6, use supportedInterfaceOrientations & shouldAutorotate instead
#ifdef __IPHONE_6_0
- (NSUInteger) supportedInterfaceOrientations{
    return UIInterfaceOrientationMaskAllButUpsideDown;
}
#endif

- (BOOL) shouldAutorotate {
    return YES;
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation {
    [super didRotateFromInterfaceOrientation:fromInterfaceOrientation];

    auto glview = cocos2d::Director::getInstance()->getOpenGLView();

    if (glview)
    {
        CCEAGLView *eaglview = (__bridge CCEAGLView *)glview->getEAGLView();

        if (eaglview)
        {
            CGSize s = CGSizeMake([eaglview getWidth], [eaglview getHeight]);
            cocos2d::Application::getInstance()->applicationScreenSizeChanged((int) s.width, (int) s.height);
        }
    }
}

//fix not hide status on ios7
- (BOOL)prefersStatusBarHidden {
    return YES;
}

// Controls the application's preferred home indicator auto-hiding when this view controller is shown.
// (better use preferredScreenEdgesDeferringSystemGestures for controlling the home indicator)
- (BOOL)prefersHomeIndicatorAutoHidden {
    return NO;
}

// HOME Indicator need to be tapped twice 
-(UIRectEdge)preferredScreenEdgesDeferringSystemGestures
{
    return UIRectEdgeBottom; 
}

- (void)didReceiveMemoryWarning {
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];

    // Release any cached data, images, etc that aren't in use.
}

static cocos2d::LuaValueDict convertNSDictionaryToLua(id object)
{
    if ([object isKindOfClass:[NSDictionary class]])
    {
        cocos2d::LuaValueDict dict;
        for(id subKey in [object allKeys])
        {
            id subValue = [object objectForKey:subKey];
            if ([subValue isKindOfClass:[NSString class]])
            {
                dict[[subKey UTF8String]] = cocos2d::LuaValue::stringValue([subValue UTF8String]);
            }
            if([subValue isKindOfClass:[NSNumber class]])
            {
                NSNumber* num = subValue;
                const char* numType = [num objCType];
                if(num == (void*)kCFBooleanFalse || num == (void*)kCFBooleanTrue)
                {
                    bool v = [num boolValue];
                    dict[[subKey UTF8String]] = cocos2d::LuaValue::booleanValue(v);
                }
                else if(strcmp(numType, @encode(float)) == 0)
                {
                    dict[[subKey UTF8String]] = cocos2d::LuaValue::floatValue([subValue floatValue]);
                }
                else
                {
                    dict[[subKey UTF8String]] = cocos2d::LuaValue::intValue([subValue intValue]);
                }
                dict[[subKey UTF8String]] = cocos2d::LuaValue::stringValue([subValue UTF8String]);
            }
            if ([subValue isKindOfClass:[NSDictionary class]])
            {
                //dict[[subKey UTF8String]] = convertNSDictionaryToLua(subValue);
                cocos2d::LuaValueDict dict2 = convertNSDictionaryToLua(subValue);
                
                dict[[subKey UTF8String]] = cocos2d::LuaValue::dictValue(dict2);
            }
        }
        
        return  dict;
    }
}

//SDK回调
-(void)backFromPass:(NSDictionary *)value{
//    NSLog(@"回调函数接收到=======%@", value.description);
//    NSString *action =value[@"action"];
//    NSString *errcode =value[@"errcode"];
//
//    if ([errcode isKindOfClass:[NSNumber class]]){
//        errcode =[value[@"errcode"] stringValue];
//    }
//    else if([errcode isKindOfClass:[NSString class]]){
//
//    }
    //NSDictionary *_data =value[@"data"];
    cocos2d::LuaValueDict item = convertNSDictionaryToLua(value);
    //item["errcode"] = cocos2d::LuaValue::stringValue([errcode UTF8String]);
    //item["action"] = cocos2d::LuaValue::stringValue([action UTF8String]);
    //select 需要重新加载游戏
//    if([action isEqualToString:@"select"]){
//        //在此重新加载
//    }
//    else if([action isEqualToString:@"version"]){
//        //送审版本号
//        //audit_version 版本号  1.1.1
//
//        if ([value[@"audit_version"] isEqualToString:@"1.2.4"]){
//            NSLog(@"此版本为送审版本");
//        }
//        else
//            NSLog(@"送审版本号=%@", value[@"audit_version"] );
//    }
//    else if([action isEqualToString:@"login"]){
//
//        //进度一致无需操作
//        //account_id 账号ID
//        //login_key  约定客户端和server交互key
//        //server_id  游戏服务器编号
//        //errcode    错误码
//        //googlefcm  推送客户端deviceToken
//        //fb_client  FB是否已绑定
//        //gc_client  GC是否已绑定
//        //serverip   游戏服务器地址
//        //server_status 服务器状态   1正常   99维护
//        //server_port  端口
//        //cocos2d::LuaValueDict item;
//        //NSString *account_id  =value[@"account_id"];
//        //item["login_key"] = cocos2d::LuaValue::stringValue([account_id UTF8String]);
//
//        //pStack->pushLuaValueDict(item);
//        //pStack->executeFunctionByHandler(self->nLuaFunc, 1);
//
//    }
//    else if([action isEqualToString:@"submit"]){
//        NSLog(@"获取用户信息");
//    }
//    else if([action isEqualToString:@"server"]){
//        //切换服务器成功，重新启动游戏
//        NSLog(@"切换服务器成功,可以重新启动游戏了");
//    }
//    else if([action isEqualToString:@"bind"]){
//        if ([errcode isEqualToString:@"0"]){
//            NSLog(@"绑定成功");
//        }
//        else if([errcode isEqualToString:@"2"]){
//            NSLog(@"取消");
//        }
//        else if([errcode isEqualToString:@"20005"]){
//            NSLog(@"当前FB账号已绑定过，绑定失败！");
//        }
//        else if([errcode isEqualToString:@"20006"]){
//            NSLog(@"当前GG账号已绑定过，绑定失败！");
//        }
//        else if([errcode isEqualToString:@"20007"]){
//            NSLog(@"当前GC账号已绑定过，绑定失败！");
//        }
//        else{
//            NSLog(@"绑定失败！");
//        }
//    }
//    else if([action isEqualToString:@"getuserinfo"]){
//        //获取玩家信息
//        //account_id    账号ID
//        //os_ver        OS版本
//        //device_name   设备名
//        //resolution    分辨率
//        //language      语言
//        //gc_client     是否绑定GC
//        //fb_client     是否绑定FB
//        //gc_alias      GC昵称
//        //fb_alias      FB昵称
//        //apns_token    推送TOKEN
//        //server_id     服务器编号
//
//
//    }
//    else if([action isEqualToString:@"switch"]){
//        if ([errcode isEqualToString:@"20001"]){ //要切换的账号不存在
//            NSLog(@"要切换的账号不存在");
//        }
//        else if([action isEqualToString:@"30001"]){//当前游戏账号是游客，没有绑定过账号，需先绑定
//            NSLog(@"当前游戏账号是游客，没有绑定过账号，需先绑定");
//        }
//        else if([action isEqualToString:@"0"]){
//            if ([value[@"account_id"] isEqualToString:value[@"target_account_id"]]){
//                //要切换的账号和当前账号相同
//                NSLog(@"要切换的账号和当前账号相同");
//            }
//            else{
//                //这里需要重新登陆
//            }
//        }
//        else
//        {
//            //切换失败
//        }
//    }
//    else if([action isEqualToString:@"payorder"]){   //充值回调
//        NSLog(@"订单");
//    }
//    else if([action isEqualToString:@"paycheck"]){   //充值回调
//        NSLog(@"errcode=%@", value[@"errcode"]);
//
//        if ([errcode isEqualToString:@"0"]){
//            NSLog(@"充值成功");
//        }
//        else{
//            NSLog(@"充值失败");
//
//        }
//
//        //0     成功
//        //2     取消
//        //4     默认失败
//        //40001  服务器不存在
//        //40002。支付条目不存在
//        //40003 订单信息出错
//        //40004 订单验证失败
//        //40005 订单信息错误，包名错误
//        //40006 订单信息错误，验证失败
//
//
//    }
//    else if([action isEqualToString:@"getserver"]){  //获取服务器列表
//        if ([errcode isEqualToString:@"0"]){
//            //server_id 服务器编号
//            //serverName。服务器名称
//            //status。  服务器状态。  1正常。  99维护
//            // 模拟数据如下：
//            //            action = getserver;
//            //            data =     (
//            //                        {
//            //                            serverName = test1;
//            //                            serverid = 1;
//            //                            status = 1;
//            //                        },
//            //                        {
//            //                            serverName = test2;
//            //                            serverid = 2;
//            //                            status = 0;
//            //                        }
//            //                        );
//            //            errcode = 0;
//            //            errmsg = success;
//            //NSDictionary * list = [value objectForKey:@"data"];
//            //cocos2d::Value serverList = FileUtils::getInstance()->convertNSObjectToCCValue(list);
//
//            //item["server"] = cocos2d::LuaValue::dictValue(serverList);
//            //NSLog(@"回调函数接收到=======%d", [list count]);
////            int i ;
////            for (i = 0 ;i<aaa ;i++)
////            {
////
////                NSDictionary * serverItem = [list objectAtIndex:i];
////                NSLog(@"回调函数接收到=======%@", serverItem.description);
////                NSString * name =[serverItem[@"serverName"] stringValue];
////                NSLog(@"回调函数接收到=======%@", name);
////            }
//
//        }
//    }
    int scriptHandler = [[LuaObjectCBridge getInstance] getScriptHandler];
    if (scriptHandler)
    {
        LuaBridge::pushLuaFunctionById(scriptHandler);
        LuaStack *stack = LuaBridge::getStack();
        stack->pushLuaValueDict(item);
        stack->executeFunction(1);
    }
    /*
     errcode 错误码
     0      请求成功
     10001  auth_code  不合法
     10002  网络出错（比如：添加数据库失败）
     10003  设备ID不合法，提交前用 md5 加密，保证长度是32位，并且没有特殊字符
     10004  openid 不合法，提交前用 md5 加密，保证长度是32位，并且没有特殊字符
     10005  账号不存在
     
     
     20001  当前账号已经绑定过 gg/gc/fb
     20002  当前账号没有绑定过 gg/gc/fb
     20005  解绑失败
     
     
     30001  要切换的账号不存在
     30002  此第3方存在账号，不能绑定。可以调用切换账号接口
     
     
     40001  支付订单出错
     40002  应用包名出错
     50001  支付失败
     40003  条目ID或礼包编号不合法
     40004  验证失败
     40005  订单号已经存在
     
     */
    
    /*
     optype
     login   登录
     select  进度选择
     unbind  解绑
     
     */
}


@end
