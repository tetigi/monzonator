# Monzonator

It-just-works style client library for doing [Monzo](https://monzo.com) stuff. 
Check out the developer docs [here.](https://monzo.com/docs/)

## Screw reading! How do I get started?

Getting started with `monzonator` is super easy!

Add the `monzonator` and `monzonator-api` libs to your project in gradle.

https://bintray.com/tetigi/Monzonator

In gradle:

`compile 'com.tetigi:monzonator:<latest-version>'`

`compile 'com.tetigi:monzonator-api:<latest-version>'`

You can see a full working example in `src/main/kotlin/com/tetigi/monzonator/Main.kt`.

Create your SSL and client configurations (you can steal the `truststore.jks` 
from this project, or use your own):

```kotlin
val sslConfig = SslConfiguration.of(Paths.get("monzonator/var/security/truststore.jks"))
val config = ClientConfigurations.of(
        listOf(MonzoService.DEFAULT_MONZO_URL),
        SslSocketFactories.createSslSocketFactory(sslConfig),
        SslSocketFactories.createX509TrustManager(sslConfig)
)
```

Create the clients themselves (for authing and making normal Monzo calls):

```kotlin
val authService = KotlinRetrofit2Client.create(MonzoAuthService::class.java, "auth", config)
val monzo = KotlinRetrofit2Client.create(MonzoService::class.java, "monzo", config)
```

Instantiate the token service that handles the auth token workflow for you:

```kotlin
// The token service handles redirects and authorization workflow
val tokenService = MonzoRefreshingTokenResource(
        "<client_id>",
        "<client_secret>",
        authService,
        URL("<the_current_service_location>") // <- will be localhost:8080 for testing
)
```

And then host that (using Dropwizard, for example):

```kotlin
env.jersey().register(HttpRemotingJerseyFeature.INSTANCE)
env.jersey().register(tokenService)
```

Finally, you can kick off the auth workflow (which generates a link, which generates an email auth request, 
which redirects back to you..)

```kotlin
tokenService.startBlockingAuthTokenRequest()
```

Then start making your calls!

```kotlin
monzo.getAccounts(tokenService.getRefreshingToken()).call()
```

## Hang on, slow down a second.. what are these actually doing?

Monzonator provides a set of convenient libs for connecting to Monzo, as well as for handling the
authorization workflow around that.

For example, making a call to get your accounts is as simple as:

```kotlin
val response = monzo.getAccounts(token).call()
println(response.accounts)
```

Every call to Monzo requires an authorization token (part of the OAuth2 workflow). You can find docs on how that 
works [here.](https://monzo.com/docs/#authentication)This token validates that you are you, and should be kept secret.

Every call is correctly typed into a nice immutable data structure from a call. An `Account` for example:

```kotlin
data class Account(
        val id: String,
        val description: String,
        val created: DateTime)
```

### The auth workflow

If you read the docs on how authing works with Monzo (or have attempted to do it yourself), you should see by now that
it's a bit of a pain in the ass. You need to handle multiple secrets, redirects and codes to get a bearer token,
which also needs to be refreshed every few hours!

Fortunately, `monzonator` has got your back. By hosting the `MonzoRefreshingTokenResource` in your Monzo app, you
can trigger the auth workflow and then conveniently get an automatically-refreshing token. No more worries!

`monzonator` handles the state and authorization codes for you - the only thing that you need to generate and pass to
`MonzoRefreshingTokenResource` are the client id and secret (which you generate through the Monzo developer API)
and the service location (which should match the redirect URL you specify in the developer API). 

For example, when running the server on `localhost`, the server URL passed to the token resource becomes
`http://localhost:8080`, and the redirect URL in the developer API is `http://localhost:8080/oauth/callback`.
