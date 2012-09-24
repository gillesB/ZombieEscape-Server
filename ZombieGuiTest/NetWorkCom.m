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

GameOrganizer* gameOrganizer;
int x ;

bool read_Ready ;


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
        [self StopReadingInputStream];
        [self initNetworkComm];
        
    }
    return self;
}

-(void)startReadingInputStream{
    read_Ready=YES;
}

-(void)StopReadingInputStream{
    read_Ready=NO;
}

-(void)setDelegate:(id)gameOrg{
    gameOrganizer=gameOrg;
}

-(void)stream:(NSStream *)stream handleEvent:(NSStreamEvent)eventCode {
   // NSLog(@"Ready : %i",x++);
    NSString *event;
    if (read_Ready && gameOrganizer != nil){
        switch(eventCode) {
        
            case NSStreamEventHasBytesAvailable:   
                event = @"Lese...";
               [gameOrganizer handleInputFromNetwork:inputStream.readLine];
                break;
            case NSStreamEventNone:
                event = @"NSStreamEventNone";
                break;
            case NSStreamEventOpenCompleted:
                event = @"NSStreamEventOpenCompleted";
                break;
            case NSStreamEventHasSpaceAvailable:
                event = @"NSStreamEventHasSpaceAvailable";
               break;
          case NSStreamEventErrorOccurred:
                event = @"NSStreamEventErrorOccurred"; 
                break;
            case NSStreamEventEndEncountered:
                event = @"NSStreamEventEndEncountered";break; 
            default:
                event = @"Unknown"; break;
                
        }
      //  NSLog(@"Event : %@",event);
    }
}



-(NSString*)readLineFromInputStream{
    return inputStream.readLine;
}


-(int) createNewPlayer:(NSString*)playerName {
    SocketMessage* msg = [SocketMessage createSocketMessageWithCommand:@"newGamer" andValue:playerName];               
    [self writeJson:msg.toJson ToStream:outputStream];
    int gamerID = [[inputStream readLine] intValue];
    NSLog(@"new gamerID = %d", gamerID);
    return gamerID;
}

-(int) createNewGame:(NSString*)gameName {
    SocketMessage* msg = [SocketMessage createSocketMessageWithCommand:@"newGame" andValue:gameName];               
   [self writeJson:msg.toJson ToStream:outputStream];
    //return the gameID received from the server
    return [[inputStream readLine] intValue];
}

-(BOOL) addPlayerToGame:(int)gameID{
    NSString* str_id =[NSString stringWithFormat:@"%d",gameID];
    SocketMessage *msg = [SocketMessage createSocketMessageWithCommand:@"addGamer" andValue:str_id];               
    [self writeJson:msg.toJson ToStream:outputStream];
    BOOL humanORzombie = [[inputStream readLine] boolValue];
    NSLog(@"Gamer is human: %d", humanORzombie);
    return humanORzombie;
}

-(BOOL) removePlayer{
    SocketMessage *msg = [SocketMessage createSocketMessageWithCommand:@"removeGamer" andValue:nil];               
    [self writeJson:msg.toJson ToStream:outputStream];
    BOOL successful = [[inputStream readLine] boolValue];
    return successful;
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

-(NSMutableArray*) getGamerlist{
        
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
    //??? Output mode changed from NSDefaultRunLoopMode to NSRunLoopCommonModes
    [inputStream.stream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSRunLoopCommonModes];
    [outputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSRunLoopCommonModes];
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
