### Description

Sample RP app project to showcase integration with PlanetID.

### Usage

Select `backendProdDevelopmentDebug` build variant to build and test the sample.

### How it works

If using the `planetid://` scheme, the app should create an intent with a specific uri for PlanetID, as described here:

- https://github.com/planetway/planet-id-demo-rp/blob/main/planet-id-demo-rp-android/app/src/main/java/com/planetway/planetid/rpsdk/PlanetId.kt#L9

The Android OS will find an activity that handles such a scheme and start it. If it doesn't exist, because PlanetID is not installed, an `ActivityNotFoundException` will be thrown and this can be used to notify the user, that PlanetID app must be installed.

For example, this is how login is implemented in the demo application:

- https://github.com/planetway/planet-id-demo-rp/blob/main/planet-id-demo-rp-android/app/src/main/java/com/planetway/demo/fudosan/ui/login/LoginViewModel.kt#L43
