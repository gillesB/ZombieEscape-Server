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
@synthesize edtIPAddress;





- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    

    // print the username from the plistfile in the editbox
    edtUserName.text = [[PlistHandler sharedHandler] getUsername];
    
    // print the server ip address from the plistfile in the other editbox
    edtIPAddress.text = [[PlistHandler sharedHandler] getServerIPAddress];
    
    //self.edtIPAddress.delegate = self;
    

}

- (void)viewDidUnload
{
    [self setEdtUserName:nil];
    [self setEdtIPAddress:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

- (IBAction)onBtnLoginClick:(id)sender {
    
    if(![[NetWorkCom getNetWorkCom] isConnected]){
        NSLog(@"%@",@"Network connection is not ready");
        [self showMessageNoNetworkConnection];
    } else if ([[edtUserName text] length] != 0){
        [self performSegueWithIdentifier: @"segLoginToMainMenu" sender: self];
        [[PlistHandler sharedHandler] setUsername:[edtUserName text]];
        [[NetWorkCom getNetWorkCom] createNewPlayer: edtUserName.text];
    } else { //Connection is ready but username is empty
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

- (void) showMessageNoNetworkConnection{
    UIAlertView *message = [[UIAlertView alloc] initWithTitle:@"Information"
                                                      message:@"No network connection. Please wait and try again or check network connection."
                                                     delegate:nil
                                            cancelButtonTitle:@"OK"
                                            otherButtonTitles:nil];
    
    [message show];
}


- (IBAction)edtIPAddressChanged:(id)sender {
    [[PlistHandler sharedHandler] setServerIPAddress:edtIPAddress.text];
    [[NetWorkCom getNetWorkCom] reconnect];
}
@end
