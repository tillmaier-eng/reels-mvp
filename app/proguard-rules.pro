# Add project specific ProGuard rules here.
# Room, Media3 and Compose all ship their own consumer ProGuard rules via
# their AARs, so this file stays intentionally small.

# Keep Room entity/DAO classes' structure for reflection-based generation.
-keep class com.personal.reels.data.model.** { *; }

# Keep the JS bridge / WebView-related classes from being renamed, in case
# a future version adds a JavascriptInterface.
-keepclassmembers class com.personal.reels.player.** {
    public *;
}
