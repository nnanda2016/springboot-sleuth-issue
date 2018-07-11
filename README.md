# springboot-sleuth-issue
A demo app to demonstrate issue with Spring Cloud Sleuth

To run the app, use following command (you need Java 8 for this)
```
./gradlew clean build bootRun
```

To test the app, you can use following URLs

**Success cases**
```
curl -v http://localhost:5002/users/U1
curl -v http://localhost:5002/user-list/?ids=U1,U2
```

**Failure cases**
```
curl -v http://localhost:5002/users/U-1
```

**Testing `@NewSpan` issue with `Mono.subscriberContext()`**
```
curl -v http://localhost:5002/users/ids/U1
```