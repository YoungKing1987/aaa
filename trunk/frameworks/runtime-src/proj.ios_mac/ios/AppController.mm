/****************************************************************************
 Copyright (c) 2010-2013 cocos2d-x.org
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

#import "AppController.h"
#import "scripting/lua-bindings/manual/CCLuaEngine.h"
#import "scripting/lua-bindings/manual/CCLuaBridge.h"
#import "cocos2d.h"
#import "AppDelegate.h"
#import "RootViewController.h"
#import "passport.h"
#import "FBSDKCoreKit/FBSDKCoreKit.h"
#import "LuaObjectCBridge.h"
using namespace cocos2d;
@implementation AppController
{
    passport * _passport;
}

@synthesize window;

#pragma mark -
#pragma mark Application lifecycle

// cocos2d application instance
static AppDelegate s_sharedApplication;
static AppController* s_sharedAppController;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
    cocos2d::Application *app = cocos2d::Application::getInstance();
    
    // Initialize the GLView attributes
    app->initGLContextAttrs();
    cocos2d::GLViewImpl::convertAttrs();
    
    // Override point for customization after application launch.

    // Add the view controller's view to the window and display.
    window = [[UIWindow alloc] initWithFrame: [[UIScreen mainScreen] bounds]];

    // Use RootViewController to manage CCEAGLView
    _viewController = [[RootViewController alloc]init];
    _viewController.wantsFullScreenLayout = YES;
    

    // Set RootViewController to window
    if ( [[UIDevice currentDevice].systemVersion floatValue] < 6.0)
    {
        // warning: addSubView doesn't work on iOS6
        [window addSubview: _viewController.view];
    }
    else
    {
        // use this method on ios6
        [window setRootViewController:_viewController];
    }

    [window makeKeyAndVisible];

    [[UIApplication sharedApplication] setStatusBarHidden:true];
    
    // IMPORTANT: Setting the GLView should be done after creating the RootViewController
    cocos2d::GLView *glview = cocos2d::GLViewImpl::createWithEAGLView((__bridge void *)_viewController.view);
    cocos2d::Director::getInstance()->setOpenGLView(glview);
    [[FBSDKApplicationDelegate sharedInstance] application:application
                             didFinishLaunchingWithOptions:launchOptions];
    
    s_sharedAppController = self;
    //run the cocos2d-x game scene
    app->run();
    self->_passport = [[passport sharedPassport] initSDK:@"https://account-mafia.5stargame.com/api/" myView:_viewController];
    return YES;
}

- (BOOL)application:(UIApplication *)application
            openURL:(NSURL *)url
            options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {
    
    BOOL handled = [[FBSDKApplicationDelegate sharedInstance] application:application
                                                                  openURL:url
                                                        sourceApplication:options[UIApplicationOpenURLOptionsSourceApplicationKey]
                                                               annotation:options[UIApplicationOpenURLOptionsAnnotationKey]
                    ];
    // Add any custom logic here.
    return handled;
}

+(void) registerScriptHandler:(NSDictionary *)dict
{
    [[LuaObjectCBridge getInstance] setScriptHandler:[[dict objectForKey:@"scriptHandler"] intValue]];
}

+(void)onLogin{
    [s_sharedAppController MyloginbyDeviceid];
}

-(void)MyloginbyDeviceid
{
    [_passport loginbyDeviceid];
}

+(void)onGetServerList
{
    [s_sharedAppController MyGetServerList];
}

-(void)MyGetServerList
{
    [_passport getServerList];
}

+(void)GetUserInfo:(NSDictionary *)dict
{
    [s_sharedAppController MyGetUserInfo:dict];
}

-(void)MyGetUserInfo:(NSDictionary *)dict
{
    [_passport Get_Userinfo:[dict objectForKey:@"accountid"]];
}


+(void)onFBSwitch{
    [s_sharedAppController MyFBSwitch];
}

-(void)MyFBSwitch{
    [_passport FB_switch];
}

+(void)onPay:(NSDictionary *)dict{
    [s_sharedAppController MyPay:dict];
}

-(void)MyPay:(NSDictionary *)dict{
    [_passport pay_product:[dict objectForKey:@"product"] pay_money:[dict objectForKey:@"money"] package_name:[dict objectForKey:@"name"] game_user_id:[dict objectForKey:@"user"]] ;
}

+(void)onServerSelect:(NSDictionary *)dict{
    [s_sharedAppController MyServerSelect:dict];
}

+(void)onFBBind{
    [s_sharedAppController MyFBBind];
}

+(void)onGCBind{
    [s_sharedAppController MyGCBind];
}

-(void)MyServerSelect:(NSDictionary *)dict{
    [_passport server_sel:[dict objectForKey:@"index"]];
}

-(void)MyFBBind{
    [_passport FB_Bind];
}

-(void)MyGCBind{
    [_passport GC_Bind];
}

+(void)onAccountUnBinding:(NSDictionary *)dict{
    [s_sharedAppController MyAccountUnBinding:dict];
}

-(void)MyAccountUnBinding:(NSDictionary *)dict{
    [_passport accountUnBinding:[dict objectForKey:@"optye"]];
}

+(void)onFB_switch{
    [s_sharedAppController MyFB_switch];
}

-(void)MyFB_switch{
    [_passport FB_switch];
}

- (void)applicationWillResignActive:(UIApplication *)application {
    /*
     Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
     Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
     */
    // We don't need to call this method any more. It will interrupt user defined game pause&resume logic
    /* cocos2d::Director::getInstance()->pause(); */
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    /*
     Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
     */
    // We don't need to call this method any more. It will interrupt user defined game pause&resume logic
    /* cocos2d::Director::getInstance()->resume(); */
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    /*
     Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
     If your application supports background execution, called instead of applicationWillTerminate: when the user quits.
     */
    cocos2d::Application::getInstance()->applicationDidEnterBackground();
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    /*
     Called as part of  transition from the background to the inactive state: here you can undo many of the changes made on entering the background.
     */
    cocos2d::Application::getInstance()->applicationWillEnterForeground();
}

- (void)applicationWillTerminate:(UIApplication *)application {
    /*
     Called when the application is about to terminate.
     See also applicationDidEnterBackground:.
     */
}


#pragma mark -
#pragma mark Memory management

- (void)applicationDidReceiveMemoryWarning:(UIApplication *)application {
    /*
     Free up as much memory as possible by purging cached data objects that can be recreated (or reloaded from disk) later.
     */
}

// 将得到的deviceToken传给SDK

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken{
    
    NSString *deviceTokenStr = [[[[deviceToken description]
                                  
                                  stringByReplacingOccurrencesOfString:@"<" withString:@""]
                                 
                                 stringByReplacingOccurrencesOfString:@">" withString:@""]
                                
                                stringByReplacingOccurrencesOfString:@" " withString:@""];
    
    NSLog(@"deviceTokenStr:\n%@",deviceTokenStr);
    
    
    
    //
    
    passport *_passport =[passport sharedPassport];
    [_passport setApns_token:deviceTokenStr];
}
// 注册deviceToken失败

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error{
    
    NSLog(@"注册deviceToken失败 -- %@",error);
    
}

- (void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)())completionHandler{
    
    //处理推送过来的数据
    
    //[self handlePushMessage:response.notification.request.content.userInfo];
    NSLog(@"PushMessage=%@", response.description);
    
    completionHandler();
    
}


- (void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler{
    
    // 需要执行这个方法，选择是否提醒用户，有Badge、Sound、Alert三种类型可以设置
    
    completionHandler(UNNotificationPresentationOptionAlert);
    
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary * _Nonnull)userInfo fetchCompletionHandler:(void (^ _Nonnull)(UIBackgroundFetchResult))completionHandler{
    
    NSLog(@"didReceiveRemoteNotification:%@",userInfo);
    
    
    if(userInfo && userInfo[@"aps"]){
        
        NSDictionary * aps =userInfo[@"aps"];
        if (aps && aps[@"alert"]){
            NSLog(@"Notification=%@", aps[@"alert"]);
        }
    }
    
    completionHandler(UIBackgroundFetchResultNewData);
    
}

- (void)redirectNotificationHandle:(NSNotification *)nf{ // 通知方法
    //NSData *data = [[nf userInfo] objectForKey:NSFileHandleNotificationDataItem];
    //NSString *str = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    
    //txtLog.text = [NSString stringWithFormat:@"%@\n\n%@",txtLog.text, str];// logTextView 就是要将日志输出的视图（UITextView）
    //NSRange range;
    //range.location = [txtLog.text length] - 1;
    //range.length = 0;
    //[txtLog scrollRangeToVisible:range];
    [[nf object] readInBackgroundAndNotify];
}

- (void)redirectSTD:(int )fd{
    NSPipe * pipe = [NSPipe pipe] ;// 初始化一个NSPipe 对象
    NSFileHandle *pipeReadHandle = [pipe fileHandleForReading] ;
    dup2([[pipe fileHandleForWriting] fileDescriptor], fd) ;
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(redirectNotificationHandle:)
                                                 name:NSFileHandleReadCompletionNotification
                                               object:pipeReadHandle]; // 注册通知
    [pipeReadHandle readInBackgroundAndNotify];
}



#if __has_feature(objc_arc)
#else
- (void)dealloc {
    [window release];
    [_viewController release];
    [super dealloc];
}
#endif


@end
