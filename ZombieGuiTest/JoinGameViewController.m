//
//  JoinGameViewController.m
//  ZombieGuiTest
//
//  Created by moco on 17.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import "JoinGameViewController.h"
#import "Socket_GameOverview.h"
#import "NetWorkCom.h"
#import "LocationManager.h"

@interface JoinGameViewController ()

@end

@implementation JoinGameViewController

@synthesize gameList;

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
 
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;

    
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void) viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    self.gameList = [[NetWorkCom getNetWorkCom] getGamelist];
    myLocation = [[LocationManager getLocationManager] myLocation];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
	return YES;
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return gameList.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView
                             dequeueReusableCellWithIdentifier:@"CustomCell"];
	Socket_GameOverview *s = [self.gameList objectAtIndex:indexPath.row];
	UILabel *nameLabel = (UILabel *)[cell viewWithTag:100];
    nameLabel.text = s.name;
    UILabel *amountPlayers = (UILabel *)[cell viewWithTag:101];
    amountPlayers.text = [[NSString alloc] initWithFormat:@"%d",s.amountGamers];
    UILabel *distance = (UILabel *)[cell viewWithTag:102];
    //TODO calculate actual distance
    CLLocation *gameLocation = [[CLLocation alloc] initWithLatitude:s.latitude longitude:s.longitude];
    CLLocationDistance distanceIn_km = [myLocation distanceFromLocation:gameLocation]/1000;
    distance.text = [NSString stringWithFormat:@"%f",distanceIn_km];
    
    
    return cell;
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }   
    else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view controller.
    /*
     <#DetailViewController#> *detailViewController = [[<#DetailViewController#> alloc] initWithNibName:@"<#Nib name#>" bundle:nil];
     // ...
     // Pass the selected object to the new view controller.
     [self.navigationController pushViewController:detailViewController animated:YES];
     */
    
    //get selected game
    Socket_GameOverview *s = [self.gameList objectAtIndex:indexPath.row];
    NSLog(@"Selected game: %d - %@", s.gameID, s.name);
    
    //add gamer to game
    //TODO give MapView information if gamer is zombie or not
    
    //TODO only join game if there is a network connection, perhaps look for general solution in NetWorkCom
    [[NetWorkCom getNetWorkCom] addPlayerToGame:s.gameID];
    
    
    [self performSegueWithIdentifier: @"segJoinGameToMapView" sender: self];
    
}


@end
