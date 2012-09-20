//
//  GPSCon.h
//  ZombieGuiTest
//
//  Created by HTWdS User on 19.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>
#import "NetWorkCom.h"
#import "GPSLocation.h"


@interface GPSCon : NSObject <CLLocationManagerDelegate>{
    CLLocationManager *locationManager;}

@end
