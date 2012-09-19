//
//  JoinGameViewController.h
//  ZombieGuiTest
//
//  Created by moco on 17.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface JoinGameViewController : UITableViewController{
    NSMutableArray *gameList;
}

@property (nonatomic, retain) NSMutableArray *gameList;

- (NSMutableArray*) createArrayToDisplay;

@end
