//
//  GPSCon.h
//  ZombieGuiTest
//
//  Created by HTWdS User on 19.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NetWorkCom.h"
#import <CoreLocation/CoreLocation.h>
#import "GPSLocation.h"


@interface GPSCon : NSObject <CLLocationManagerDelegate>{
    CLLocationManager *locationManager;}

@property (weak,nonatomic) NetWorkCom* netCom;

@end
