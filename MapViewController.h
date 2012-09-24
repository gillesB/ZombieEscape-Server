//
//  MapViewController.h
//  ZombieEscape
//
//  Created by HTWdS User on 22.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//


#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import "GameOrganizer.h"
#import "NetWorkCom.h"


@interface MapViewController :  UIViewController <CLLocationManagerDelegate,MKMapViewDelegate>{
    CLLocationManager *locationManager;}

@property (weak, nonatomic) IBOutlet MKMapView *mapView;
@property (strong, nonatomic) IBOutlet UILabel *locationLabel;

-(void)drawGamers:(NSMutableArray*)PlayerLocation;

@end
