//
//  gcManager.h
//  passportSDK
//
//  Created by hkl on 2018/5/4.
//  Copyright © 2018年 hkl. All rights reserved.
//
//

#import <Foundation/Foundation.h>
#import <GameKit/GameKit.h>
#import <StoreKit/StoreKit.h>
#import <UserNotifications/UserNotifications.h>


@class ViewController;
@interface gcManager : NSObject<GKGameCenterControllerDelegate, SKProductsRequestDelegate, SKPaymentTransactionObserver>
{
  UIActivityIndicatorView *_activityIndicatorView;
}
@property (retain,nonatomic) UIActivityIndicatorView * activityIndicatorView;

+(gcManager *)sharedgcManager;
-(BOOL)isGameCenterAvailable;
-(void)authenticateLocalUser;
-(void)registerFoeAuthenticationNotification;
-(void)initRootViewController:(UIViewController*)myRootViewController;
-(void) initStoreKit;
-(gcManager *)initGC;
-(void)Gc_Bind;
-(void)buy:(NSString *) buyType;
-(void)myfinishTransaction:(NSString *)orderid;
@end


