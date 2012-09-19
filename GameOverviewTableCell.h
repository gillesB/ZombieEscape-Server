//
//  GameOverviewTableCell.h
//  ZombieGuiTest
//
//  Created by moco on 19.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface GameOverviewTableCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *lblGameName;
@property (weak, nonatomic) IBOutlet UILabel *lblAmountGamers;
@property (weak, nonatomic) IBOutlet UILabel *lblDistance;

@end
