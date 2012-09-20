//
//  Socket_GameOverview.m
//  ZombieGuiTest
//
//  Created by moco on 17.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import "Socket_GameOverview.h"

@implementation Socket_GameOverview
@synthesize gameID, name, amountGamers, longitude, latitude;

- (id) initWithGameID:(int)g_id name:(NSString *)n amountGamers:(int)am_ga longitude:(double)longi latitude:(double)lati{
    self.gameID = g_id;
    self.name = n;
    self.amountGamers = am_ga;
    self.longitude = longi;
    self.latitude = lati;
    return self;
}

-(NSDictionary*) toJson {
    
    
    return nil;
    
}

+(id) FromJsonToObject:(NSString *)jString{
    
    
    return nil;
}

@end
