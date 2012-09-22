//
//  MapViewController.h
//  ZombieEscape
//
//  Created by HTWdS User on 22.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//


#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>


NSInputStream *inputStream;
NSOutputStream *outputStream;

@interface MapViewController :  UIViewController <CLLocationManagerDelegate,MKMapViewDelegate>{
    CLLocationManager *locationManager;}

@property (weak, nonatomic) IBOutlet MKMapView *MapView;
@property (strong, nonatomic) IBOutlet UILabel *LocationLabel;

@end
