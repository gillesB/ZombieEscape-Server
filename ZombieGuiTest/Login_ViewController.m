//
//  ViewController.m
//  ZombieGuiTest
//
//  Created by Jochen on 12.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import "Login_ViewController.h"
#import "PlistHandler.h"

@interface Login_ViewController ()

@end

@implementation Login_ViewController
@synthesize edtUserName;
@synthesize netCom = _netCom;





- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    

    // dump the username
    NSString* username = [[PlistHandler sharedHandler] getUsername];
    [edtUserName setText:username];
    _netCom = [NetWorkCom getNetWorkCom];

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
        [[PlistHandler sharedHandler] setUsername:[edtUserName text]];
        [_netCom createNewPlayer: edtUserName.text];
    } else {
        NSLog(@"%@",@"Username is empty");
        [self showMessageNoUserName];
    }
}

- (void) showMessageNoUserName{
    UIAlertView *message = [[UIAlertView alloc] initWithTitle:@"Information"
                                                      message:@"Please enter a Username."
                                                     delegate:nil
                                            cancelButtonTitle:@"OK"
                                            otherButtonTitles:nil];
    
    [message show];
}


@end
