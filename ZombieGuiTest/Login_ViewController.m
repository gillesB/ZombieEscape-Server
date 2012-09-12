//
//  ViewController.m
//  ZombieGuiTest
//
//  Created by Jochen on 12.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import "Login_ViewController.h"

@interface Login_ViewController ()

@end

@implementation Login_ViewController
@synthesize edtUserName;

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
}

- (void)viewDidUnload
{
    [self setEdtUserName:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

- (IBAction)onBtnLoginClick:(id)sender {
    if ([[edtUserName text] length] != 0){
        [self performSegueWithIdentifier: @"segLoginToMainMenu" sender: self];
    } else {
        NSLog(@"%@",@"Username is empty");
    }
}
@end
