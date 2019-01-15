//
//  passport.h
//  passportSDK
//
//  Created by hkl on 2018/5/3.
//  Copyright © 2018年 hkl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "gcManager.h"
#import "FBManager.h"
#import <UIKit/UIKit.h>
@protocol FromPassDelegate<NSObject>;
-(void)backFromPass:(NSDictionary *)value;
@end


@interface passport : NSObject{
    
    id<FromPassDelegate> delegate;
    
}
@property gcManager *_gcManager;
@property FBManager *_fbManager;

@property (nonatomic, retain)id<FromPassDelegate>delegate;


@property NSString *URLString;
@property NSString *userid;
@property NSString *device_id;
@property NSString *user_idfa;
@property NSString *app_key;
@property NSString *server_id;
@property NSString *gc_openid;
@property NSString *gc_alias;
@property NSString *gc_email;
@property NSString *gc_client;
@property NSString *fb_openid;
@property NSString *fb_alias;
@property NSString *fb_email;
@property NSString *fb_client;
@property NSString *target_user_id;

@property NSString *device_ver;
@property NSString *phone_model;
@property NSString *resolving;
//@property NSString *operator;
@property NSString *language;
@property NSString *app_version;
@property NSString *apns_token;
@property NSString *country ;

@property NSString *auto_bind;


+(passport *)sharedPassport;

-(passport *)initSDK:(NSString*)url myView:(UIViewController*)myRootViewController;
-(void)loginbyDeviceid;
-(void)loginbySysInit;
-(void)loginbyFB;

-(void)FB_Bind;
-(void)GC_Bind;
-(void)GC_Bind_;
-(void)BindbyFB;
-(void)progressSelect;
-(void)accountUnBinding:(NSString *)optype;

-(void)Get_Userinfo:(NSString *)game_userid;

-(void)pay_product:(NSString *) pro_id pay_money:(NSString *)pay_money package_name:(NSString *)package_name game_user_id:(NSString *)game_user_id;
-(void)pay_success:(NSString *) gcsign sandbox:(NSString *)sandbox;

-(void)server_sel:(NSString *) server_id;

-(void)FB_switch;
-(void)FB_switch_;

-(void)getServerList;
-(void)callback_general:(NSString *)action errcode:(NSString *)errcode;

@end


