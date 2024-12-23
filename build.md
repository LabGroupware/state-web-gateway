# 手動Build手順

``` sh
./gradlew bootBuildImage --imageName=ablankz/nova-web-gateway:1.0.7
docker push ablankz/nova-web-gateway:1.0.7
```