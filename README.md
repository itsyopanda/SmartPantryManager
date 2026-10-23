# Smart Pantry Manager

A Java Android application that helps users track pantry ingredients and find recipes that can be made strictly from ingredients already available.

## Technology
- Java and Android Studio
- XML layouts
- SQLiteOpenHelper for persistent local storage
- RecyclerView with custom adapters

## Features
- Add, read, update and delete pantry ingredients
- Optional expiry date
- 20 seeded recipes
- Strict recipe matching: all required ingredients and quantities must be available
- Recipe detail screen with ingredients and preparation steps
- Settings preference saved locally
- Input validation and empty-state messages

## Open and run
1. Install Android Studio and a compatible JDK bundled with Android Studio.
2. In Android Studio, choose **Open** and select this project folder.
3. Allow Gradle sync to complete. If prompted, install Android SDK 35.
4. Create an Android emulator using Device Manager, or connect an Android device with USB debugging enabled.
5. Click **Run**.

## Database
The app creates `smart_pantry.db` on first launch. Recipes are seeded automatically. Pantry data remains on the device between app sessions.

## Strict matching notes
Names are normalized for case and common plural differences. Units must match within supported equivalent labels, and the pantry quantity must be at least the recipe requirement. This starter version does not convert between different unit types such as grams and kilograms.
