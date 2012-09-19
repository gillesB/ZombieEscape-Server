//
//  GameOverviewTableCell.m
//  ZombieGuiTest
//
//  Created by moco on 19.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import "GameOverviewTableCell.h"

@implementation GameOverviewTableCell
@synthesize lblGameName;
@synthesize lblAmountGamers;
@synthesize lblDistance;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
