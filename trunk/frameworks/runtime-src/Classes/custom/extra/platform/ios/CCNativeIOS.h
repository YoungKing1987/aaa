
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#include "native/CCAlertViewDelegate.h"

#if CC_LUA_ENGINE_ENABLED > 0
#include "scripting/lua-bindings/manual/CCLuaEngine.h"
#endif

USING_NS_CC;
USING_NS_CC_EXTRA;

@interface CCNativeIOS : NSObject < UIAlertViewDelegate >
{
    UIActivityIndicatorView *activityIndicatorView_;
    
    UIAlertView *alertView_;
    CCAlertViewDelegate *alertViewDelegates_;
#if CC_LUA_ENGINE_ENABLED > 0
    LUA_FUNCTION alertViewLuaListener_;
#endif
}

+ (CCNativeIOS *)sharedInstance;


#pragma mark -
#pragma mark activity indicator

- (void)showActivityIndicator:(UIActivityIndicatorViewStyle)style;
- (void)hideActivityIndicator;


#pragma mark -
#pragma mark alert view

- (void)createAlertView:(NSString *)title
             andMessage:(NSString *)message
   andCancelButtonTitle:(NSString *)cancelButtonTitle;
- (NSInteger)addAlertButton:(NSString *)buttonTitle;
- (void)showAlertViewWithDelegate:(CCAlertViewDelegate *)delegate;
- (void)removeAlertView;
- (void)cancelAlertView;

#if CC_TARGET_PLATFORM == CC_PLATFORM_IOS
- (void)showAlertViewWithObjcDelegate:(id<UIAlertViewDelegate>)delegate;
#endif

#if CC_LUA_ENGINE_ENABLED > 0
- (void)showAlertViewWithLuaListener:(LUA_FUNCTION)listener;
- (void)removeAlertViewLuaListener;
#endif

#pragma mark -
#pragma mark UIAlertView delegates

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex;

@end