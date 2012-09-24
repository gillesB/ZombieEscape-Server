//
//  GameOrganizer.m
//  ZombieEscape
//
//  Created by HTWdS User on 23.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import "GameOrganizer.h"
#import "SBJsonParser.h"
#import "PlayerLocation.h"
#import "PlistHandler.h"


@implementation GameOrganizer

@synthesize gamerName = _gamerName;
@synthesize gamerStatus = _gamerStatus;

NetWorkCom* netCom;
MapViewController* delegate;

BOOL pollMode ;
volatile bool run;

+ (id)getGameOrganizer {
    static GameOrganizer *gameOrg = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        gameOrg = [[self alloc] init];
        
    });
    return gameOrg;
}

- (id)init{
    if (self = [super init]) {
               
    }
    return self;
}


-(void)updateMyLocation:(CLLocation*)newLocation{
    [netCom setLocation:[[GPSLocation alloc]initWithLong:[newLocation coordinate].longitude AndLat:[newLocation coordinate].latitude ]];
}

-(void)startWithpollingMode:(BOOL)poll andDelegate:(MapViewController*)cont{
    pollMode=poll;
    netCom =[NetWorkCom getNetWorkCom] ;
    _gamerName = [[PlistHandler getPlistHandler] getUsername];
    
    if(poll){
        NSLog(@"Polling Mode....");
        [self pollingLifeCicle];
    }else {
        NSLog(@"Non Polling Mode, waiting for Networkevents");
        [self setdelegate:cont];
        [ netCom startReadingInputStream];
        [ netCom setDelegate:self];
    }

}

-(void)stop{
    [self stopLifeCicle];
    [netCom setDelegate:nil];
    [self setdelegate:nil];
    [netCom StopReadingInputStream];
}


-(void)stopLifeCicle{
    run =NO;
}

-(void) pollingLifeCicle{
    run =true;
    while (run){
       NSLog(@" Von Server zu GameOrganizer : %@ ", [netCom readLineFromInputStream]);
    }
}

-(void) setdelegate:(MapViewController*)viewController {
    delegate = viewController;
}

-(void)handleInputFromNetwork:(NSString*)stream{
    
    // Create SBJSON object to parse JSON
    SBJsonParser *parser = [[SBJsonParser alloc] init];
    NSDictionary *dic = [parser objectWithString:stream];
   
    NSLog(@"Von Server : %@",[dic description]);
    NSString* commandString = [ dic objectForKey:@"command"];
    NSMutableArray* gamerLocations = [[NSMutableArray alloc] initWithCapacity:1];
    
    if ([commandString compare:@"listGamers"] ==0){
        
        NSArray* gamerDescription = [dic valueForKey:@"value"];
        for (NSDictionary* dictionary in gamerDescription ){
            CLLocationCoordinate2D location = [[[CLLocation alloc] init ] coordinate];
                        
            double longi = [[dictionary valueForKey:@"longitude"]doubleValue ];
            double lat = [[dictionary valueForKey:@"latitude"]doubleValue ];
            location.longitude = longi ;
            location.latitude =lat;
            
            NSString* gamerName =[dictionary valueForKey:@"gamername"];
          //  if( ! [gamerName compare:_gamerName]==0){
                PlayerLocation* gamerloc = [[PlayerLocation alloc] initWithName:gamerName address:@"BRAIN" coordinate:location];
                [gamerLocations addObject:gamerloc];
           // }
        }
        [delegate drawGamers:gamerLocations];
        
    }else if ([commandString compare:@"fight"]==0){
        NSLog (@" ATTACKE !! !! ! !!");
    }
    else {
        NSLog(@"unbekanntes commando : %@", commandString);
    }
     
}

@end
