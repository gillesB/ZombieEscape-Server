//
//  GPSLocation.m
//  Map
//
//  Created by HTWdS User on 12.09.12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "GPSLocation.h"


@implementation GPSLocation 
@synthesize longitude;
@synthesize latitude;



-(NSDictionary*) toJson {
          
    return [NSDictionary dictionaryWithObjectsAndKeys: [NSNumber numberWithDouble:longitude] ,@"longitude",[NSNumber numberWithDouble:latitude] ,@"latitude", nil ];

}

+(id) FromJsonToObject:(NSString *)objectString{
    
    NSMutableString* s = [[NSMutableString alloc]initWithString:objectString];
 
    
    return [[NSObject alloc]init];
}

-(GPSLocation*)initWithLong:(double)longi AndLat:(double)lat{
    self = [super init];
    
    if(self){
        self.longitude =longi;
        self.latitude =lat;
    }
    return self;
}

@end
