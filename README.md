## Overview
This is a general-purpose Java library designed to empower enterprise-level development. It includes common enums, business exceptions, data transfer objects, and frequently used utilities. The library only depends on lombok and validation-api, making it extremely lightweight.

Includes: Common Enums, Exception Handling, Data Models, and various common utility classes.

## Enums
Provides common enums such as: YesNo, DeleteStatus, EnableStatus, ValidationPattern, WeekEnum, etc.
Provides a safe cached enum translation utility: EnumResolver

## Exception Handling
Provides common business exception classes: BusinessException and ExceptionUtils.
ExceptionUtils is used to extract exception stack information for logging frameworks such as log4j

## Data Models
Provides common contract entities: Req, DeviceInfo, ReqMetadata, Res, PageQuery, PagedData, etc.
Provides tuple encapsulations: Pair, Single, Triple
Provides common transfer objects: Id, IdAction, MultiId, etc.

## Enhanced Utility Tools
### Conversion Utilities
Base62, CollKit, ConvertUtils, FormatUtils, HexUtils, RmbUtils, StringUtils, UriUtils

### Validation Utilities
AuditDiffUtils, IdCardUtils, MachineId

### Security Utilities
AESUtils, CaptchaUtils, CryptoUtils, DataMaskUtils, ECCUtils, LuhnUtils, PasswordHash, PasswordValidator, RsaUtils, SignTool

### Network Utilities
DingTalkRobot, FileUploaderV1, FileUploaderV2, HttpUtilsV1, HttpUtilsV2, IpV4Utils, RobotNotifier

### Math Utilities
BitFieldUtils, BitFlagUtils, MathUtils, RandomUtils, SnowId

### Time Utilities
DateUtils, DelaySimulator, WebTimeUtils

### Validation Utilities
ValidateUtils

### IO Utilities
FilePathUtils, FileUtils, ImageUtils, ZipUtils

### System Utilities
ClassScanner, LruCache, SingleAppLock, SqlUtils

## How to Use
#### In Maven:
```agsl
  <repositories>
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
      </repository>
    </repositories>
    <dependencies>
      <dependency>
        <groupId>com.github.hwpok</groupId>
        <artifactId>commons</artifactId>
        <version>1.0.6</version>
      </dependency>
    </dependencies>
```
#### In Gradle
```agsl
    repositories {
      mavenCentral()
      maven { url = uri("https://jitpack.io") }
    }
    dependencies {
      implementation("com.github.hwpok:commons:1.0.6")
    }
```




