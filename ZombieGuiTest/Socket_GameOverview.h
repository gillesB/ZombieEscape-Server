//
//  Socket_GameOverview.h
//  ZombieGuiTest
//
//  Created by moco on 17.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Socket_GameOverview : NSObject{
    NSInteger *gameID;
    NSString *name;
    NSInteger *amountGamers;
    double longitude;
    double latitude;
}

@property(nonatomic) NSInteger *gameID;
@property(nonatomic, copy) NSString *name;
@property(nonatomic) NSInteger *amountGamers;
@property(nonatomic) double longitude;
@property(nonatomic) double latitude;

- (id)initWithGameID:(NSInteger*) gameID name:(NSString*) name amountGamers:(NSInteger*) amountGamers longitude:(double) longitude latitude:(double) latitude;



@end
