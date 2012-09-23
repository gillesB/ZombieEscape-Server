//
//  GameOrganizer.m
//  ZombieEscape
//
//  Created by HTWdS User on 23.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import "GameOrganizer.h"


@implementation GameOrganizer

@synthesize netCom=_netCom;

bool pollingMode ;
volatile bool run;

+ (id)getGameOrganizer:(bool)pollingMode {
    static GameOrganizer *gameOrg = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        gameOrg = [[self alloc] init];
    });
    return gameOrg;
}

- (id)init {
    if (self = [super init]) {
        _netCom =[NetWorkCom getNetWorkCom] ;
        
        if(pollingMode){
            NSLog(@"Polling Mode");
            [NSThread detachNewThreadSelector:@selector(pollingLifeCicle:) toTarget:[GameOrganizer class] withObject:nil];
        }else {
            NSLog(@"Non Polling Mode, waiting for Networkevents");
            [_netCom startReadingInputStream];
        }
        
    }
    return self;
}

-(void)stopLifeCicle{
    run =NO;
}

-(void) pollingLifeCicle{
    run =YES;
    while (run){
       NSLog(@" Von Server zu GameOrganizer : %@ ", [_netCom readLineFromInputStream]);
        
    }
}

@end
