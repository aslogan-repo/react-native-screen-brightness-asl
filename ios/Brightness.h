#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#if __has_include(<React/RCTBridgeModule.H>)
#import <React/RCTBridgeModule.h>
#import <React/RCTBridge.h>
#import <React/RCTEventDispatcher.h>
#else
#import "RCTBridgeModule.h"
#import "RCTBridge.h"
#import "RCTEventDispatcher.h"
#endif

@interface Brightness : NSObject <RCTBridgeModule>

@end
