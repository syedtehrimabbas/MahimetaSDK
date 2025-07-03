# Mahimeta Ad SDK

[![](https://jitpack.io/v/syedtehrimabbas/MahimetaSDK.svg)](https://jitpack.io/#syedtehrimabbas/MahimetaSDK)

A lightweight Android SDK for displaying ads with dynamic configuration from your Mahimeta dashboard.

## Features

- Dynamic ad unit ID configuration
- Multiple ad size support
- Lifecycle-aware ad management
- Easy integration with both XML and Jetpack Compose

## Features

- Dynamic ad unit ID configuration
## Installation

### Step 1: Add JitPack Repository

Add the JitPack repository to your project-level `settings.gradle`:

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url 'https://jitpack.io' }
        google()
        mavenCentral()
    }
}
```

### Step 2: Add Dependency

Add the dependency to your app-level `build.gradle`:

```groovy
dependencies {
    implementation 'com.github.syedtehrimabbas:MahimetaSDK:1.0.0'
}
```

### Step 3: Add Required Permissions

Add these permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="com.google.android.gms.permission.AD_ID" />
```

### Step 4: Add Your Publisher ID

Add your Mahimeta publisher ID to your `AndroidManifest.xml`:

```xml
<application>
    <meta-data
        android:name="com.mahimeta.sdk.PUBLISHER_ID"
        android:value="YOUR_PUBLISHER_ID" />
</application>
```

## Prerequisites

1. Add your Mahimeta publisher ID to your app's `AndroidManifest.xml`:

```xml
<manifest>
    <application>
        <meta-data
            android:name="com.mahimeta.sdk.PUBLISHER_ID"
            android:value="YOUR_PUBLISHER_ID" />
    </application>
</manifest>
```

2. Add internet permission to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Usage

### 1. Initialize the SDK

Initialize the SDK in your `Application` class:

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MahimetaSDK.initialize(this)
    }
}
```

### 2. Using in XML Layouts

Add the `MahimetaAdView` to your layout:

```xml
<com.mahimeta.sdk.MahimetaAdView
    android:id="@+id/adView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:adSize="BANNER" />
```

### 3. Using in Jetpack Compose

```kotlin
@Composable
fun AdBanner() {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            MahimetaAdView(context).apply {
                setAdSize(MahimetaAdSize.BANNER)
            }
        }
    )
}
```

### 4. Available Ad Sizes

- `BANNER` - Standard 320x50 banner
- `LARGE_BANNER` - Large 320x100 banner
- `MEDIUM_RECTANGLE` - Medium 300x250 rectangle
- `FULL_BANNER` - 468x60 banner
- `LEADERBOARD` - 728x90 leaderboard
- `ADAPTIVE_BANNER` - Adaptive banner (width of the screen)

To set a custom size programmatically:

```kotlin
val adView = findViewById<MahimetaAdView>(R.id.adView)
adView.setAdSize(MahimetaAdSize.ADAPTIVE_BANNER) // or any other size
```

## Advanced Usage

### Manual Ad Refresh

```kotlin
adView.loadAd() // Manually load an ad
adView.reloadAd(30000) // Reload ad after 30 seconds
```

### Lifecycle Integration

The `MahimetaAdView` automatically handles lifecycle events, but you can also manage them manually:

```kotlin
// In your Activity/Fragment
override fun onResume() {
    super.onResume()
    adView.onResume()
}

override fun onPause() {
    adView.onPause()
    super.onPause()
}

override fun onDestroy() {
    adView.onDestroy()
    super.onDestroy()
}
```

## Error Handling

The SDK handles most errors internally and logs them with the tag "MahimetaAdView". You can listen for ad events:

```kotlin
adView.setAdListener(object : AdListener() {
    override fun onAdLoaded() {
        // Ad loaded successfully
    }

    override fun onAdFailedToLoad(error: LoadAdError) {
        // Handle ad loading failure
    }
    
    // Other callbacks available: onAdOpened(), onAdClicked(), onAdClosed(), onAdImpression()
})
```

## ProGuard/R8

If you're using ProGuard or R8, add these rules to your `proguard-rules.pro`:

```
# Keep AdMob classes
-keep public class com.google.android.gms.ads.** { *; }
-keep public class com.google.ads.** { *; }

# Keep Mahimeta SDK classes
-keep class com.mahimeta.sdk.** { *; }
-keepclassmembers class com.mahimeta.sdk.** { *; }
```

## License

```
Copyright 2025 Mahimeta

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
