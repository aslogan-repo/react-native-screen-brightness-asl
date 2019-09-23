#import "Brightness.h"
#if __has_include(<React/RCTBridgeModule.H>)
#else
#import "RCTBridgeModule.h"
#import "RCTBridge.h"
#import "RCTEventDispatcher.h"
#endif

@implementation Brightness

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(hasPermission:(RCTPromiseResolveBlock)resolve
                  hasPermissionRejector:(RCTPromiseRejectBlock)reject) {
    resolve(@(true));
}
RCT_EXPORT_METHOD(requestPermission:(RCTPromiseResolveBlock)resolve
                  requestPermissionRejector:(RCTPromiseRejectBlock)reject)
{
    resolve(@(true));
}

RCT_EXPORT_METHOD(getBrightness:(RCTPromiseResolveBlock)resolve
                  getScreenBrightnessRejector:(RCTPromiseRejectBlock)reject) {

    float brightness = [[UIScreen mainScreen] brightness];
    resolve(@(brightness));
}

RCT_REMAP_METHOD(setBrightness,
                 brightnessLevel:(CGFloat)brightnessLevel
                 setSystemBrightnessResolver:(RCTPromiseResolveBlock)resolve
                 setSystemBrightnessRejector:(RCTPromiseRejectBlock)reject) {
    [[UIScreen mainScreen] setBrightness:brightnessLevel];
    resolve(@(brightnessLevel));
}

RCT_EXPORT_METHOD(getBrightnessMode:(RCTPromiseResolveBlock)resolve
                  getBrightnessModeRejector:(RCTPromiseRejectBlock)reject) {

    resolve(@"auto");
}

RCT_REMAP_METHOD(setBrightnessMode,
                 mode:(NSString *)brightnessMode
                 setBrightnessModeResolver:(RCTPromiseResolveBlock)resolve
                 setBrightnessModeRejector:(RCTPromiseRejectBlock)reject) {
    resolve(@(true));
}

@end
