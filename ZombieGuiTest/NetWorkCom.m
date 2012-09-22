//
//  NetWorkCom.m
//  ZombieGuiTest
//
//  Created by HTWdS User on 19.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import "NetWorkCom.h"
#import "SocketMessage.h"
#import "PlistHandler.h"
#import "Classes/SBJson.h"
#import "Socket_GameOverview.h"


@implementation NetWorkCom



+ (id)getNetWorkCom {
    static NetWorkCom *netCom = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        netCom = [[self alloc] init];
    });
    return netCom;
}

- (id)init {
    if (self = [super init]) {
        [self initNetworkComm];
    }
    return self;
}


-(void)stream:(NSStream *)stream handleEvent:(NSStreamEvent)eventCode {
    
    switch(eventCode) {
        
        case NSStreamEventHasBytesAvailable:   
        {
            
            /*uint8_t buf[1024];
            unsigned int len = 0;
            len = [(NSInputStream *)stream read:buf maxLength:1024];
            if(len) {
                NSLog(@"%@",[[NSString alloc] initWithBytes:buf length:1024 encoding:NSASCIIStringEncoding])  ;
            }*/
            NSLog(@"message from server: %@",inputStream.readLine);
            break;
        }
            
    }
}




-(void) createNewPlayer:(NSString*)playerName {
    SocketMessage* msg = [SocketMessage createSocketMessageWithCommand:@"newGamer" andValue:playerName];               
    [self writeJson:msg.toJson ToStream:outputStream];
}

-(void) createNewGame:(NSString*)gameName {
    SocketMessage *msg = [SocketMessage createSocketMessageWithCommand:@"newGame" andValue:gameName];               
    [self writeJson:msg.toJson ToStream:outputStream];
}

-(void) addPlayerToGame:(NSString*)gameID{
    SocketMessage *msg = [SocketMessage createSocketMessageWithCommand:@"addGamer" andValue:gameID];               
    [self writeJson:msg.toJson ToStream:outputStream];
}

-(void) removePlayer{
    SocketMessage *msg = [SocketMessage createSocketMessageWithCommand:@"removeGamer" andValue:nil];               
    [self writeJson:msg.toJson ToStream:outputStream];
}

-(void) setLocation:(GPSLocation*)loc{
    SocketMessage *msg = [SocketMessage createSocketMessageWithCommand:@"setLocation" andValue: loc];               
    [self writeJson:msg.toJson ToStream:outputStream];
}

-(NSMutableArray*) getGamelist{
    SocketMessage *msg = [SocketMessage createSocketMessageWithCommand:@"listGames" andValue: nil];
    [self writeJson:msg.toJson ToStream:outputStream];
    NSString* json = [inputStream readLine];
    NSLog(@"from server lsgames %@", json);
    
    // Create SBJSON object to parse JSON
    SBJsonParser *parser = [[SBJsonParser alloc] init];
    NSArray *id_array = [parser objectWithString:json];
    
    NSMutableArray *gameArray = [[NSMutableArray alloc] initWithCapacity:[id_array count]];
    
    for (int i = 0; i < [id_array count];i++) {
        NSDictionary *d = [id_array objectAtIndex:i];
        int gameID = [[d objectForKey:@"gameID"] intValue];
        NSString* name = [d objectForKey:@"name"];
        double amount = [[d objectForKey:@"amountGamers"] doubleValue];
        double longi = [[d objectForKey:@"longitude"] doubleValue];
        double lati = [[d objectForKey:@"latitude"] doubleValue];
        
        
        Socket_GameOverview *s = [[Socket_GameOverview alloc] initWithGameID:gameID name:name amountGamers:amount longitude:longi latitude:lati];
        
        [gameArray addObject:s];
        
    }

    return gameArray;
    
}


-(void)writeJson:(NSDictionary*)dict ToStream:(NSOutputStream*)stream{
    
    NSError *error = nil; 
    
    if (![NSJSONSerialization isValidJSONObject:dict])
        @throw [NSException exceptionWithName:@"NotJsonConfirm" reason:@"NotJsonConfirm" userInfo:nil];
    
    //NSLog( [[NSString alloc] initWithData:json encoding:NSUTF8StringEncoding] );     
    
    NSInteger err = [NSJSONSerialization writeJSONObject: dict toStream:stream options:kNilOptions  error:&error];
    
    // sendinge newline 
    uint8_t newline = 0x0A;  
    
    [outputStream write: &newline maxLength:1];
    
}

- (void)initNetworkComm {
    CFReadStreamRef readStream;
    CFWriteStreamRef writeStream;
    CFStringRef ipAddress = (__bridge CFStringRef)([[PlistHandler getPlistHandler] getServerIPAddress]);
    
    CFStreamCreatePairWithSocketToHost(NULL, ipAddress, 2004, &readStream, &writeStream);
    NSInputStream* input =(__bridge NSInputStream *)readStream;
    inputStream = [[MDBufferedInputStream alloc] initWithInputStream:input bufferSize:1024 encoding:NSUTF8StringEncoding];
    outputStream = (__bridge NSOutputStream *)writeStream;
    [inputStream.stream setDelegate:self];
    [outputStream setDelegate:self];
    [inputStream.stream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    [outputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    [inputStream open];
    [outputStream open];
    
}

- (BOOL) isConnected {
    if (inputStream.streamStatus == NSStreamStatusOpen){
        return YES;
    } else {
        return NO;
    }
}

-(void) closeConnection{
    [inputStream close];
    [outputStream close];
}

-(void) reconnect{
    [self closeConnection];
    [self initNetworkComm];
}



@end
