//
//  ViewController.h
//  ZombieGuiTest
//
//  Created by Jochen on 12.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface Login_ViewController : UIViewController
- (IBAction)onBtnLoginClick:(id)sender;
- (void) showMessageNoUserName;
@property (weak, nonatomic) IBOutlet UITextField *edtUserName;

@end
