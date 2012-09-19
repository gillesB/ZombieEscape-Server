 //
//  SocketMessage.m
//  Map
//
//  Created by HTWdS User on 11.09.12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SocketMessage.h"



@implementation SocketMessage

@synthesize command = _command;
@synthesize value = _value;


-(NSDictionary*) toJson {
    if(self.command ==nil)
        @throw [NSException exceptionWithName:@"CommandIsNilException" reason:@"" userInfo:nil];
    
    NSDictionary* dict=nil;
    
    if (self.value == nil)
        dict = [NSDictionary dictionaryWithObjectsAndKeys: self.command,@"command",@"null",@"value", nil ]; 
    else if ( [self.value conformsToProtocol: @protocol( JsonSerializable ) ] )
        dict = [NSDictionary dictionaryWithObjectsAndKeys: self.command,@"command",[(NSObject<JsonSerializable>* )self.value toJson],@"value", nil ]; 
    else if  ( [self.value isKindOfClass:[NSString class]] ) 
        dict = [NSDictionary dictionaryWithObjectsAndKeys: self.command,@"command",self.value,@"value", nil ];
    else
        @throw [NSException exceptionWithName:@"UnhandableValueObject" reason:[[self.value description] stringByAppendingString:@" is neither a String nor an Object that implements the protocoll JsonSerializable"] userInfo:nil];
    
    return dict;
}

+(id) FromJsonToObject:(NSObject *)obj{
    
    return [[NSObject alloc]init];
}

+(SocketMessage *) createSocketMessageWithCommand:(NSString *)com andValue:(NSObject*)val{
   
    if (com == nil)
        @throw [NSException exceptionWithName:@"CommandIsNil" reason:@"" userInfo:nil];
    SocketMessage* sockMes = [[SocketMessage alloc ]init ];
    
    sockMes.command = com;
    
    if ( val==nil || [val conformsToProtocol: @protocol( JsonSerializable ) ] || [val isKindOfClass:[NSString class]] )
        sockMes.value=val;
    else 
        @throw [NSException exceptionWithName:@"UnhandableValueObject" reason:[[val description] stringByAppendingString:@" is neither a String nor an Object that implements the protocoll JsonSerializable"] userInfo:nil];
         
    return sockMes;

}

@end
