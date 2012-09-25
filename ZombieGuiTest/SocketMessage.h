//
//  SocketMessage.h
//  Map
//
//  Created by HTWdS User on 11.09.12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "JsonSerializable.h"

@interface SocketMessage : NSObject <JsonSerializable>
@property (strong,nonatomic) NSString *command;
@property (strong,nonatomic) NSObject *value;
+(SocketMessage *) createSocketMessageWithCommand:(NSString *)com andValue:(NSObject*)val;
@end
