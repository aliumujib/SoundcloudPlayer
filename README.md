# GETMe podcasts
An Android demonstration app to get podcasts and playlists from SoundCloud.

With some random animations.

**Dependencies**

* RxJava 2
* Retrofit 2
* Rebound
* Gson
* Picasso
* Recycler View

## How to build
Go to the root folder and run the command:
```sh
$ gradlew assemble
```
You can find the builds (release and debug) in the directory `[root]/app/build/outputs/apk`.

## Improvements to-do list

* Use Dagger2 for dependency injection (for DataManager for example)
* Espresso UI tests
