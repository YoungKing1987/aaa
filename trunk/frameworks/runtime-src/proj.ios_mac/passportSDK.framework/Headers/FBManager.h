//
//  FBManager.h
//  passportSDK
//
//  Created by hkl on 2018/5/24.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>


@interface FBManager : NSObject


+(FBManager *)sharedfbManager;
-(void)initRootViewController:(UIViewController*)myRootViewController;
//-(FBManager *)initFB:(UIViewController*)myRootViewController;
-(FBManager *)SwitchFB;

-(FBManager *)BindFB;


@end
