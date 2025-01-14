# 手動Build手順

``` sh
BUILD_VERSION=1.0.12
./gradlew clean build jibMultiBuild -PimageVersion=$BUILD_VERSION
docker push ablankz/nova-web-gateway:$BUILD_VERSION-amd64
docker push ablankz/nova-web-gateway:$BUILD_VERSION-arm64
```