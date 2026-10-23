# Beginner setup and testing guide

## Project setup
Open this folder in Android Studio. Use the Android Studio bundled JDK if Gradle asks for a JDK. Wait for Gradle sync to finish. If SDK 35 is missing, install it from Tools > SDK Manager.

## Run
Create an emulator in Tools > Device Manager, choose a recent Pixel device and download a system image if requested. Press the green Run button.

## First test
1. Launch the app.
2. Tap Add ingredient and enter `bread`, quantity `2`, unit `pieces`.
3. Add `cheese`, quantity `2`, unit `slices`.
4. Tap Find recipes. Cheese Toastie should appear.
5. Open it and check the ingredients and method.
6. Return to pantry and edit bread quantity to `1`. Cheese Toastie must disappear because the recipe needs 2 pieces.
7. Add `tomato`, quantity `1`, unit `pieces`, and `salt`, quantity `1`, unit `g`; Tomato Toast should appear if you also add bread with quantity 2.
8. Delete an ingredient and verify recipes requiring it disappear.
9. Close and reopen the app; pantry items should remain.
10. Try saving an empty ingredient name or zero quantity to test validation.

## GitHub (make real commits as you work)
1. Create a public repository named `SmartPantryManager`.
2. In Android Studio, open View > Tool Windows > Terminal.
3. Configure Git if needed:
   `git config --global user.name "Your Name"`
   `git config --global user.email "your-github-email"`
4. In the project folder, run:
   `git init`
   `git add .`
   `git commit -m "Create Android Java project structure"`
5. Add the GitHub repository as the remote and push using GitHub's current instructions.
6. Make future commits after genuine milestones. Do not create fake backdated commits; the assignment expects a genuine development history.

## Before submission
- Capture real screenshots from the running app.
- Record a narrated 5–7 minute demonstration.
- Write the report based on what you tested and the real challenges you encountered.
- Put the Android Studio project, video and report into one ZIP file. Keep the ZIP below 50 MB.
