//
//  GameOrganizer.h
//  ZombieEscape
//
//  Created by HTWdS User on 23.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NetWorkCom.h"


@interface GameOrganizer : NSObject

@property (strong ,nonatomic )NetWorkCom* netCom;

+(id)getGameOrganizer:(BOOL)pollingmode;
-(void)stopLifeCicle;
@end
