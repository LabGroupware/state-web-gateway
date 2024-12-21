# 手動Build手順

``` sh
./gradlew bootBuildImage --imageName=ablankz/nova-web-gateway:1.0.6
docker push ablankz/nova-web-gateway:1.0.6
```