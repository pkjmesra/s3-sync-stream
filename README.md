[![Java Version](http://img.shields.io/badge/Java-1.8-blue.svg)](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
[![Codeship Status for julianghionoiu/s3-sync](https://img.shields.io/codeship/b617e390-006f-0135-fe1b-4ee982914aba/master.svg)](https://codeship.com/projects/212588)
[![Coverage Status](https://coveralls.io/repos/github/julianghionoiu/s3-sync/badge.svg?branch=master)](https://coveralls.io/github/julianghionoiu/s3-sync?branch=master)

The purpose of this library is to continuously upload (multi-part) files that are being generated in a folder.
The library is optimised for working with the video files generated by:
https://github.com/julianghionoiu/dev-screen-record

#### Configuration

Configuration for running this service should be placed in file `.private/aws-test-secrets` in Java Properties file format. For examples.

```properties
aws_access_key_id=ABCDEFGHIJKLM
aws_secret_access_key=ABCDEFGHIJKLM
s3_region=ap-southeast-1
s3_bucket=bucketname
s3_prefix=prefix
```

The available values are
1. `aws_access_key_id`
    This contains the access key to the AWS account.
2. `aws_secret_access_key`
    This contains the secret key to the AWS account.
3. `s3_region`
    This contains the region that holds the S3 bucket.
4. `s3_bucket`
    This contains the bucket that will store the uploaded files.
5. `s3_prefix`
    This contains optional prefix for the base storage.

#### To build and run
```bash
./gradlew shadowJar
java -jar ./build/libs/s3-sync-1.0-SNAPSHOT-all.jar upload -f $PATH_TO_REC/recording.mp4
```

### To use as a library


Configure the local folder as a `source` and define AWS S3 as the `destination`
```java
Source source = Source.getBuilder(/* Path */ pathToFolder)
  .traverseDirectories(true)
  .include(endsWith(".mp4")
  .exclude(startsWith(".")
  .exclude(matches("tmp.log"))
  .create();

Destination destination = Destination.getBuilder()
  .loadFromPath(/* Path */ pathToFile)
  .create();
```

Construct the `RemoteSync` and run. The `run` method can be invoked multiple times.
```java
remoteSync = new RemoteSync(source, destination);
remoteSync.run();
```

#### Example source definitions

The source will be a set of filters that can be applied to a folder to obtain a list of files to be synced

**Default values** will not include .lock files and hidden files (. files)
```java
Source source = Source.getBuilder(/* Path */ pathToFolder)
  .includeAll()
  .create();
```

**Single file** can be selected using a matcher
```java
Filter filter = Filter.getBuilder().matches("file.txt");

Source source = Source.getBuilder(/* Path */ pathToFolder)
  .include(filter)
  .create();
```

**Multiple files** can be included if they match one of the matchers.
The list of included files can be further filtered via exclude matchers
```java
Filter includeFilter = Filter.getBuilder()
                        .endsWith(".mp4")
                        .endsWith(".log")
                        .create();

Filter excludeFilter = Filter.getBuilder()
                        .matches("tmp.log")
                        .create();

Source source = Source.getBuilder(/* Path */ pathToFolder)
  .include(includeFilter)
  .exclude(excludeFilter)
  .create();
```

By default the library will not **traverse directories**, if you need this behaviour than you can set the `traverseDirectories` flag to true
```java
Source source = Source.getBuilder(/* Path */ pathToFolder)
  .traverseDirectories(true)
  .includeAll()
  .create();
```

If no include matcher is specified then an **IllegalArgumentException** will be raised upon creation:
```java
Source source = Source.getBuilder(/* Path */ pathToFolder)
  .create();
```

