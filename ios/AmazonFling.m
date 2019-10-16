#import "AmazonFling.h"

@implementation AmazonFling

RCT_EXPORT_MODULE()


- (NSArray<NSString *> *)supportedEvents
{
  return @[@"device_list"];
}


RCT_EXPORT_METHOD(startSearch)
{
    RCTLogWarn(@"rctwarn startSearch");
    [self sendEventWithName:@"device_list" body:@{@"devices": @"[{\"hello\":\"one\"}]"}];
}

RCT_EXPORT_METHOD(stopSearch)
{
    RCTLogWarn(@"rctwarn stopSearch");
}

//final String targetUuid, final String name, final String title
RCT_EXPORT_METHOD(fling:(NSString *)targetUuid name:(NSString *)name title:(NSString *)title)
{
    RCTLogWarn(@"rctwarn fling");
}

RCT_EXPORT_METHOD(doPlay:(NSString *)targetUuid )
{
    RCTLogWarn(@"rctwarn doPlay");
}

RCT_EXPORT_METHOD(doPause:(NSString *)targetUuid )
{
    RCTLogWarn(@"rctwarn doPause");
}
RCT_EXPORT_METHOD(doStop:(NSString *)targetUuid )
{
    RCTLogWarn(@"rctwarn doStop");
}

@end
