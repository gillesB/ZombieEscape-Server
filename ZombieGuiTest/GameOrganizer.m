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

+ (id)getGameOrganizer:(BOOL)pollingMode {
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
            [self pollingLifeCicle];
        }else {
            [_netCom startReadingInputStream];
        }
        
    }
    return self;
}

-(void)stopLifeCicle{
    run =NO;
}

-(void) pollingLifeCicle{
    
    while (run){
        [_netCom readLineFromInputStream];
        
        
    }
}

@end
