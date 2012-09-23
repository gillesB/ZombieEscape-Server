//
//  LocationManager.m
//  ZombieEscape
//
//  Created by moco on 23.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import "LocationManager.h"

@implementation LocationManager

@synthesize myLocation;

+ (id)getLocationManager {
    static LocationManager *locMgt = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        locMgt = [[self alloc] init];
    });
    return locMgt;
}

- (id)init {
    if (self = [super init]) {
        [self initLocationManager];
    }
    return self;
}

- (void) initLocationManager{
    locationManager = [[CLLocationManager alloc] init];
    [locationManager setDelegate:self];
    //??? this is probably the desired accuracy?
    [locationManager setDesiredAccuracy:kCLLocationAccuracyBest];
    
    [locationManager startUpdatingLocation];
}

- (void)locationManager:(CLLocationManager *)manager
    didUpdateToLocation:(CLLocation *)newLocation
           fromLocation:(CLLocation *)oldLocation
{
    myLocation = newLocation;
}


- (void)locationManager:(CLLocationManager *)manager
       didFailWithError:(NSError *)error
{
	NSLog(@"Error: %@", [error description]);
}


@end
