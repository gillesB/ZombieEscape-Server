//
//  Username.m
//  ZombieGuiTest
//
//  Created by moco on 15.09.12.
//  Copyright (c) 2012 HTW Saarland. All rights reserved.
//

#import "PlistHandler.h"
@interface PlistHandler (hidden)
    - (NSString*) getPlistPath;
    - (NSMutableDictionary*) readOrCreatePList;
    - (void) savePlist;

@end

//TODO enter Server IP manually


@implementation PlistHandler (hidden)
    
    - (NSString*) getPlistPath{
        // get the path to the "Documents" directory
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *documentsDirectory = [paths objectAtIndex:0];
        
        // get the path to our plist ("Documents/foo.plist")
        NSString *plistPath = [documentsDirectory stringByAppendingPathComponent:@"ZombieGuiTest.plist"];
        return plistPath;
    }
    
    - (NSMutableDictionary*) readOrCreatePList{
        // read or create plist
        
        NSMutableDictionary *dict;
        // check if our plist already exists in the Documents directory...
        NSFileManager *fileManager = [NSFileManager defaultManager];
        if ( [fileManager fileExistsAtPath:plistPath] ) {
            // ...if it does, read it
            NSLog(@"dict existed, reading %@", plistPath);
            dict = [NSMutableDictionary dictionaryWithContentsOfFile:plistPath];
        } else {
            // ...if it doesn't, create it
            NSLog(@"dict didn't exist, creating...");
            dict = [NSMutableDictionary dictionaryWithCapacity:2];
            
            // Fill the dictionary with default values, either by copying
            // a default plist from our bundle to the Documents directory
            // or simply creating a new dictionary and writing it to th
            // Documents directory. Here we choose to create a new
            // dictionary rather than providing a default plist in the bundle.
            
            // create a NSNumber object containing the
            // integer value 1 and add it as 'key1' to the dictionary.
            [dict setObject:@"" forKey:@"username"];
            [dict setObject:@"192.168.2.101" forKey:@"serverIPAddress"];
            
            // write dictionary to Documents directory...
            NSLog(@"writing to %@...", plistPath);
            [dict writeToFile:plistPath atomically:YES];
        }
        
        return dict;


    }
    
    - (void) savePlist{
        // write xml representation of dictionary to a file
        [plistDictionary writeToFile:plistPath atomically:NO];
    }

@end

@implementation PlistHandler




#pragma mark Singleton Methods

+ (id)getPlistHandler {
    static PlistHandler *sharedHandler = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedHandler = [[self alloc] init];
    });
    return sharedHandler;
}

- (id)init {
    if (self = [super init]) {
        plistPath = [self getPlistPath];
        plistDictionary = [self readOrCreatePList];
    }
    return self;
}

- (void)dealloc {
    // Should never be called, but just here for clarity really.
}

- (NSString* ) getUsername{
    return [plistDictionary valueForKey:@"username" ];
}

- (void) setUsername:(NSString *)username{
    [plistDictionary setValue: username forKey:@"username"];
    [self savePlist];
    NSLog(@"New username set: %@", username);
}

- (NSString* ) getServerIPAddress{
    return [plistDictionary valueForKey:@"serverIPAddress" ];
}

- (void) setServerIPAddress:(NSString *)serverIPAddress{
    [plistDictionary setValue: serverIPAddress forKey:@"serverIPAddress"];
    [self savePlist];
    NSLog(@"New serverIPAddress set: %@", serverIPAddress);
}

@end
