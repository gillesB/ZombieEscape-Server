//
//  Username.h
//  ZombieGuiTest
//
//  Created by moco on 15.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface PlistHandler : NSObject
{
    NSString* plistPath;
    NSMutableDictionary* plistDictionary;
}

@property (nonatomic, retain) NSString *username;

- (NSString* ) getUsername;
- (void) setUsername:(NSString *)username;

- (NSString* ) getServerIPAddress;
- (void) setServerIPAddress:(NSString *)serverIPAddress;

+ (id)sharedHandler;

@end
