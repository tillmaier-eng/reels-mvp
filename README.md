# Reels — TikTok/Reels-style Hybrid Video App

Local videos + YouTube links in one vertical swipe feed. Personal-use
first, but built clean enough to extend later.

## চালানোর নিয়ম

1. Android Studio (Koala বা নতুন) দিয়ে এই ফোল্ডারটা **Open** করুন — এই
   প্রজেক্টে আসল Gradle Wrapper (`gradlew`, `gradlew.bat`,
   `gradle/wrapper/gradle-wrapper.jar`) থাকায় Android Studio নিজে থেকেই
   সব ডাউনলোড/সিঙ্ক করে নেবে, কোনো ম্যানুয়াল ফিক্স লাগবে না।
2. প্রথমবার sync হতে একটু সময় লাগবে (Gradle 8.7 + Android SDK 35
   ডাউনলোড হবে যদি আগে থেকে না থাকে)।
3. উপরে সবুজ ▶ বাটনে চাপুন — emulator বা USB-কানেক্টেড ফোনে চলবে।

কমান্ড লাইন থেকে বিল্ড করতে চাইলে:
```bash
./gradlew assembleDebug
```
আউটপুট APK পাবেন: `app/build/outputs/apk/debug/app-debug.apk`

## GitHub Actions

`.github/workflows/android.yml` প্রতি push-এ `main` ব্রাঞ্চে:
1. JDK 17 সেটআপ করে
2. `./gradlew assembleDebug` চালিয়ে debug APK বানায়
3. APK-টা workflow run-এর **Artifacts** সেকশনে আপলোড করে (নাম:
   `reels-debug-apk`, ১৪ দিন রিটেনশন)

## আর্কিটেকচার (MVVM)
```
data/model        -> FeedItem (Room entity), YouTubeUrlParser
data/local         -> Room DAO + Database
data/repository    -> FeedRepository (single source of truth)
di/                -> AppContainer (manual dependency injection, no Hilt)
player/            -> LocalVideoPlayer (ExoPlayer), YouTubePlayer (WebView + IFrame API)
ui/feed/           -> FeedViewModel, ReelsFeedScreen (VerticalPager + favorite/delete overlay)
ui/add/            -> AddVideoScreen (Photo Picker + YouTube URL input)
ui/theme/          -> Dark Material 3 theme
```

## গুরুত্বপূর্ণ Trade-off সিদ্ধান্ত

1. **YouTube playback = WebView + অফিসিয়াল IFrame Player API**, কোনো
   stream extraction বা yt-dlp-জাতীয় ডাউনলোডার নেই — এটাই একমাত্র
   পদ্ধতি যা YouTube-এর Terms of Service মেনে চলে। Trade-off: WebView
   native ExoPlayer surface-এর চেয়ে ভারী, তাই YouTube আইটেম pause করা
   হয় toggle-এ, প্রতি swipe-এ destroy/recreate করা হয় না।

2. **Manual DI (`di/AppContainer.kt`)**, Hilt/Dagger বাদ — annotation
   processing time ও method count কমানোর জন্য, ছোট স্কেলে worth it।

3. **System Photo Picker** ব্যবহার — READ_MEDIA_VIDEO permission
   dialog লাগে না, শুধু নির্বাচিত ফাইলগুলোর access পাওয়া যায়
   (`takePersistableUriPermission` দিয়ে app restart-এর পরও access থাকে)।

4. **শুধু settled page-ই active** — `ReelsFeedScreen`-এ pager-এর
   `settledPage` ব্যবহার করা হয় যাতে drag করার সময় দুইটা ভিডিও একসাথে
   play না হয়, এবং সবসময় সর্বোচ্চ একটা ExoPlayer/WebView active থাকে
   (battery + RAM সাশ্রয়ের মূল কৌশল)।

5. **Coil/Glide, Retrofit/OkHttp বাদ** — এই স্কেলে দরকার নেই, ছোট APK
   রাখার জন্য ইচ্ছাকৃতভাবে বাদ দেওয়া হয়েছে।

## এখনো যা নেই (ইচ্ছাকৃতভাবে, MVP-first)
- Search, watch history, continue watching
- Playlist ম্যানেজমেন্ট UI (category ফিল্ড ডেটা মডেলে আছে, UI নেই)
- Cloud sync / Drive / NAS / Plex-Jellyfin
- Recommendation system
- Local video thumbnail extraction (repository layer-এ জায়গা রাখা আছে)

## প্রয়োজনীয়তা
- Android Studio Koala (2024.1.1) বা নতুন
- JDK 17 (Android Studio-এর বান্ডেল করা JDK যথেষ্ট)
- Android SDK Platform 35 + Build-Tools (প্রথম sync-এ Android Studio
  নিজে থেকে নামিয়ে নেবে)
- minSdk 24 (Android 7.0+), compileSdk/targetSdk 35
