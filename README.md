# JARVIS AI — Android starter project

This is a free starter project for an Android voice assistant with a male TTS voice (uses voices installed on the phone), speech recognition, YouTube search, and a coding-help shortcut.

## Important
- This is source code, **not a prebuilt APK**.
- Android screen tapping/scrolling is NOT enabled in this starter. Reliable phone-wide control requires an AccessibilityService, explicit user opt-in, and careful safety limits.
- Speech recognition may use Google's speech service and may need internet.
- The AI coding button opens ChatGPT in your browser. A truly conversational AI inside the app needs an AI provider/API; free access and quotas can change. Never put a private API key inside an APK.
- YouTube opens search results; it does not guarantee automatic/background playback.

## Build
Recommended: Android Studio on a computer.
1. Extract this ZIP.
2. Open the `JARVIS_AI_Android_Starter` folder in Android Studio.
3. Allow Gradle sync.
4. Connect your Android phone with USB debugging enabled, or use an emulator.
5. Press Run.
6. To create an APK: **Build > Build APK(s)**. Android Studio shows the APK path.

On an Android phone only, building a full Android Studio project is more difficult. Termux + Android SDK/Gradle setup can be done but is not a beginner-friendly guaranteed path.

## Commands
- "YouTube kholo"
- "Atif Aslam songs chalao"
- "Coding help"
- "ChatGPT kholo"
- "Settings kholo"

## Voice
The app requests the device's speech recognizer and uses an installed English/Urdu voice if available. In Android Settings > Text-to-speech output, select an available male voice if the installed engine offers one. Not every device/locale has a male voice.
