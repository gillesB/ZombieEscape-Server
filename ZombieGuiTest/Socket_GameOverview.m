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

- (id) initWithGameID:(NSInteger *)gameID name:(NSString *)name amountGamers:(NSInteger*)amountGamers longitude:(double)longitude latitude:(double)latitude{
    self.gameID = gameID;
    self.name = name;
    self.amountGamers = amountGamers;
    self.longitude = longitude;
    self.latitude = latitude;
    return self;
}

@end
