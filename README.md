# react-native-brightness

## Getting started

`$ npm install react-native-screen-brightness-asl --save`

### Mostly automatic installation

`$ react-native link react-native-screen-brightness-asl`

### Manual installation

#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-brightness` and add `Brightness.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libBrightness.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`

- Add `import com.aslogan.reactlibrary.brightness.BrightnessPackage;` to the imports at the top of the file
- Add `new BrightnessPackage()` to the list returned by the `getPackages()` method

2. Append the following lines to `android/settings.gradle`:
   ```
   include ':react-native-screen-brightness-asl'
   project(':react-native-screen-brightness-asl').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-brightness/android')
   ```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
   ```
     compile project(':react-native-screen-brightness-asl')
   ```

## Usage

```javascript
import Brightness from "react-native-screen-brightness-asl";

Brightness.getBrightness((brightnessLevel) => {
  // brightnessLevel is a float (0 - 1)
});

Brightness.setBrightness(0.7);
```
