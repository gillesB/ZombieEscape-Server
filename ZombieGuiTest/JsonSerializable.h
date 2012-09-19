//
//  JsonSerializable.h
//  Map
//
//  Created by HTWdS User on 12.09.12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol JsonSerializable <NSObject>
-(NSDictionary*) toJson;
+(id)FromJsonToObject:(NSObject*)obj;
@end
