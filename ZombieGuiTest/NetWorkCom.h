//
//  NetWorkCom.h
//  ZombieGuiTest
//
//  Created by HTWdS User on 19.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "GPSLocation.h"


NSInputStream *inputStream;
NSOutputStream *outputStream;

@interface NetWorkCom : NSObject

-(void) createNewPlayer:(NSString*)playerName ;
-(void) createNewGame:(NSString*)gameName ;
-(void) addPlayerToGame:(NSString*)gameID;
-(void) removePlayer;
-(void) setLocation:(GPSLocation*)loc;

-(BOOL) isConnected;

+ (id)getNetWorkCom;



@end
