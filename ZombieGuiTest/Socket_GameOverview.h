//
//  Socket_GameOverview.h
//  ZombieGuiTest
//
//  Created by moco on 17.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Socket_GameOverview : NSObject{
    int gameID;
    NSString *name;
    int amountGamers;
    double longitude;
    double latitude;
}

@property(nonatomic) int gameID;
@property(nonatomic, copy) NSString *name;
@property(nonatomic) int amountGamers;
@property(nonatomic) double longitude;
@property(nonatomic) double latitude;

- (id)initWithGameID:(int) gameID name:(NSString*) name amountGamers:(int) amountGamers longitude:(double) longitude latitude:(double) latitude;



@end
