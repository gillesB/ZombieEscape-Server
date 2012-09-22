//
//  Create_GameControllerViewController.h
//  ZombieGuiTest
//
//  Created by moco on 22.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface Create_GameViewController : UIViewController
@property (weak, nonatomic) IBOutlet UITextField *edtGameName;
@property (weak, nonatomic) IBOutlet UIButton *btnCreateGame;
- (IBAction)btnCreateGameOnClick:(id)sender;

- (void) showMessageNoGameName;

@end
