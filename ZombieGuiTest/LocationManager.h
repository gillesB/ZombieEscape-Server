//
//  LocationManager.h
//  ZombieEscape
//
//  Created by moco on 23.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>


@interface LocationManager : NSObject <CLLocationManagerDelegate> {
    CLLocationManager* locationManager;

}

@property (readonly,nonatomic) CLLocation* myLocation;

- (void)locationManager:(CLLocationManager *)manager
    didUpdateToLocation:(CLLocation *)newLocation
           fromLocation:(CLLocation *)oldLocation;

- (void)locationManager:(CLLocationManager *)manager
       didFailWithError:(NSError *)error;


+ (id)getLocationManager;


@end
