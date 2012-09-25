//
//  PlayerLocation.h
//  ZombieEscape
//
//  Created by HTWdS User on 22.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <MapKit/MapKit.h>

@interface PlayerLocation : NSObject <MKAnnotation> 
//{
//    NSString *_name;
//    NSString *_address;
//    CLLocationCoordinate2D _coordinate;
//    BOOL decomission;
//}

@property (copy) NSString *name;
@property (copy) NSString *address;
@property (nonatomic, readonly) CLLocationCoordinate2D coordinate;
@property BOOL decomission;

- (id)initWithName:(NSString*)name address:(NSString*)address coordinate:(CLLocationCoordinate2D)coordinate;


@end