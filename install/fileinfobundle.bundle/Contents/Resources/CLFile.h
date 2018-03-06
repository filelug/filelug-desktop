#import <Foundation/Foundation.h>

@interface CLFile : NSObject

- (BOOL)isAliasFile:(NSString *)filePath;

// Make sure the file is alias by using [CLFile isAliasFile];
// if file is alias, return the absolute path, un-escaped one.
// if file is not alias, return nil
- (NSString *)resolveAliasFile:(NSString *)filePath;

@end
