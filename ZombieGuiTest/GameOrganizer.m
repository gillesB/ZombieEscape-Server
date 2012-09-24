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


@implementation GameOrganizer

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

-(void)startWithpollingMode:(BOOL)poll andDelegate:(MapViewController*)cont{
    pollMode=poll;
    netCom =[NetWorkCom getNetWorkCom] ;
    
    if(poll){
        NSLog(@"Polling Mode");
        [self pollingLifeCicle];
        //[NSThread detachNewThreadSelector:@selector(pollingLifeCicle:) toTarget:[GameOrganizer class] withObject:nil];
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
   
       
    
    NSString* commandString = [ dic objectForKey:@"command"];
    
   
    if ([commandString compare:@"listGamers"] ==0){
        
        NSMutableArray *gamerLocations = [[NSMutableArray alloc] initWithCapacity:1];
        NSArray* gamerDescription = [dic valueForKey:@"value"];
        for (NSDictionary* dict in gamerDescription ){
            CLLocationCoordinate2D location = [[[CLLocation alloc] init ] coordinate];
                        
            double longi = [[dic valueForKey:@"longitude"]doubleValue ];
            double lat = [[dic valueForKey:@"latiude"]doubleValue ];
            location.longitude = longi ;
            location.latitude =lat;
            
            NSString* gamerName =[dic valueForKey:@"gamername"];
            PlayerLocation* gamerloc = [[PlayerLocation alloc] initWithName:gamerName address:@"BRAIN" coordinate:location];
            [gamerLocations addObject:gamerloc];
        }
        [delegate drawGamers:gamerLocations];
        
    }else if ([commandString compare:@"fight"]==0){
        NSLog (@" ATTACKE !!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
    else {
        NSLog(@"unbekanntes commando : %@", commandString);
    }
    
    /*        
            for (int i = 0; i < [id_array count];i++) {
        NSDictionary *d = [id_array objectAtIndex:i];
        int gameID = [[d objectForKey:@"gameID"] intValue];
        NSString* name = [d objectForKey:@"name"];
        double amount = [[d objectForKey:@"amountGamers"] doubleValue];
        double longi = [[d objectForKey:@"longitude"] doubleValue];
        double lati = [[d objectForKey:@"latitude"] doubleValue];
         */     
    
    
  
     
}

@end
