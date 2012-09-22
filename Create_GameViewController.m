//
//  Create_GameControllerViewController.m
//  ZombieGuiTest
//
//  Created by moco on 22.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import "Create_GameViewController.h"
#import "NetWorkCom.h"

@interface Create_GameViewController ()

@end

@implementation Create_GameViewController
@synthesize edtGameName;
@synthesize btnCreateGame;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
}

- (void)viewDidUnload
{
    [self setBtnCreateGame:nil];
    [self setEdtGameName:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
	return YES;
}

- (IBAction)btnCreateGameOnClick:(id)sender {
    NSString* gamename = [edtGameName text];
    if ([gamename length] != 0){
        //TODO perform segue to mapview, via joinGame (low prio)
        
        int gameID = [[NetWorkCom getNetWorkCom] createNewGame: gamename];
        [[NetWorkCom getNetWorkCom] addPlayerToGame:gameID];

        
    } else { //Connection is ready but gamename is empty
        NSLog(@"%@",@"GameName is empty");
        [self showMessageNoGameName];
    }
    
    
}

- (void) showMessageNoGameName{
    UIAlertView *message = [[UIAlertView alloc] initWithTitle:@"Information"
                                                      message:@"Please enter a Gamename."
                                                     delegate:nil
                                            cancelButtonTitle:@"OK"
                                            otherButtonTitles:nil];
    
    [message show];
}
@end
