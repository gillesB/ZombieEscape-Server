//
//  GPSCon.m
//  ZombieGuiTest
//
//  Created by HTWdS User on 19.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import "GPSCon.h"


@implementation GPSCon
CLLocation* lastUpdatedLocation ;
@synthesize netCom =_netCom;

-(NetWorkCom *) netcom{
    if(self.netCom == nil)
        self.netCom = [NetWorkCom getNetWorkCom];
    return self.netCom;
}

- (void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation{
    
    if ([lastUpdatedLocation distanceFromLocation:newLocation] > 5 ){
        lastUpdatedLocation = newLocation;
        [_netCom setLocation:[[GPSLocation alloc] initWithLong:[newLocation coordinate].longitude AndLat:[newLocation coordinate].latitude]];
    }
}

-(id)init {
    locationManager = [[CLLocationManager alloc] init];
    locationManager.delegate = self;
    locationManager.distanceFilter = kCLDistanceFilterNone; // whenever we move
    // locationManager.desiredAccuracy = kCLLocationAccuracyHundredMeters; // 100 m
    [locationManager startUpdatingLocation];  
}

@end
