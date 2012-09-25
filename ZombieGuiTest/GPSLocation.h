//
//  GPSLocation.h
//  Map
//
//  Created by HTWdS User on 12.09.12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "JsonSerializable.h"

@interface GPSLocation : NSObject <JsonSerializable>
@property  double longitude;
@property  double latitude;
-(GPSLocation*)initWithLong:(double)longi AndLat:(double)lat;
@end
