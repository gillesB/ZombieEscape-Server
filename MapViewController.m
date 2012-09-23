//
//  MapViewController.m
//  ZombieEscape
//
//  Created by HTWdS User on 22.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//
#import "MapViewController.h"
#import "SocketMessage.h"
#import "GPSLocation.h"
#import "PlayerLocation.h"
#import "NetWorkCom.h"


@implementation MapViewController


@synthesize MapView =_MapView;
@synthesize LocationLabel= _LocationLabel;
@synthesize gameOrg = _gameOrg;


- (void)drawPlayer {
    for (id<MKAnnotation> annotation in _MapView.annotations) {
        [_MapView removeAnnotation:annotation];
    }
    CLLocationCoordinate2D coordinate;
    coordinate.latitude = 49.23393;
    coordinate.longitude = 6.980;            
    PlayerLocation *annotation = [[PlayerLocation alloc] initWithName:@"Zombie" address:@"BRAIN!" coordinate:coordinate] ;
    [_MapView addAnnotation:annotation];    
}


- (void)viewDidLoad{
    
    self.MapView.mapType = MKMapTypeSatellite;
    _MapView.showsUserLocation = YES;
    
    locationManager = [[CLLocationManager alloc] init];
    locationManager.delegate = self;
    locationManager.distanceFilter = kCLDistanceFilterNone; // whenever we move
    // locationManager.desiredAccuracy = kCLLocationAccuracyHundredMeters; // 100 m
    [locationManager startUpdatingLocation]; 
    
    [self defineRegion];
    
      
    [self drawPlayer];
    _gameOrg = [GameOrganizer getGameOrganizer:NO];
    
}


- (void)locationManager:(CLLocationManager *)manager
    didUpdateToLocation:(CLLocation *)newLocation
           fromLocation:(CLLocation *)oldLocation{
    
    self.LocationLabel.text = newLocation.description;
}


- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id <MKAnnotation>)annotation {
    
    static NSString *identifier = @"MyLocation";   
    if ([annotation isKindOfClass:[PlayerLocation class]]) {
        
        MKPinAnnotationView *annotationView = (MKPinAnnotationView *) [_MapView dequeueReusableAnnotationViewWithIdentifier:identifier];
        if (annotationView == nil) {
            annotationView = [[MKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:identifier];
        } else {
            annotationView.annotation = annotation;
        }
        annotationView.enabled = YES;
        annotationView.canShowCallout = YES;
        
        return annotationView;
    }
    return nil;    
}


-(void)defineRegion{
    MKCoordinateRegion region;
    region.center.latitude = 49.23700;
    region.center.longitude = 6.98000;
    region.span.latitudeDelta =0.005;
    region.span.longitudeDelta =0.005;
    
    [self.MapView setRegion:region animated:NO];
}


- (void)viewDidUnload
{
    [self setMapView:nil];
    [self setLocationLabel:nil];
    [_gameOrg stopLifeCicle];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    
    [[NetWorkCom getNetWorkCom] removePlayer];
    
}


-(void)didReceiveMemoryWarning{
    [super didReceiveMemoryWarning];
}


- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation{
    return YES;
}


@end
