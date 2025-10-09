<img width="505" height="128" alt="logo" src="https://github.com/user-attachments/assets/272af339-23d9-4afc-abd8-2cb255665cf9" />

WinlatorXR is a port of Winlator for Meta Quest and Pico headsets. It uses 2D/VR hybrid app flow documented by [amwatson](https://github.com/amwatson/2DVrHybrid).

# History of the project

* Winlator is originally developed by [brunodev85](https://github.com/brunodev85/winlator) but as he does not opensource the latest versions then I am forced to use a custom fork for this project.
* In 2024 WinlatorXR was based on Glibc fork by [longjunyu2](https://github.com/longjunyu2/winlator) but as he stopped communicating, the project was inactive for a long time and later it stopped working.
* In 2025 [Pipetto-crypto](https://github.com/Pipetto-crypto/winlator) ported Winlator to Bionic/Proton which is a massive boost for Meta Quest integration.
* His integration was enhanced by [coffincolors](https://github.com/coffincolors/winlator) in Winlator CMOD fork which is now base for WinlatorXR.
* Pico support and XR enhancement were brought by [Tobbe85](https://github.com/tobbe85/winlator).
* Play for Dream support was brought by [EasonZxp](https://github.com/EasonZxp/WinlatorXR).
* Content is maintained by [StevenMXZ](https://github.com/StevenMXZ/Winlator-Contents) who maintains it for Winlator-Bionic.

![WinlatorXR history](https://github.com/user-attachments/assets/947c4be7-fe9e-435e-92c9-9ca1d55bef79)


# How to compile

1.Clone repository with all submodules
`git clone --recursive git@github.com:lvonasek/winlator.git`

2.Donwload the latest APK from releases

3.Unzip the APK and copy the content of assets into app/src/main/assets/ (except dexopt folder)

4.Open the project in Android Studio and have fun :)

# WinlatorXR API
![API flow diagram](https://github.com/user-attachments/assets/4e511519-6987-40d8-a360-f93b92867566)

Our XrAPI provides developers with a way to replace OpenVR or OpenXR in their applications, enabling PCVR content to run natively within WinlatorXR on standalone VR headsets. The XrAPI is still very experimental and may change over time.

### Required changes for the Windows app
1) WinlatorXR checks for Z:\tmp\xr\vr file. If this file exists, it enters VR mode and start sending data.
2) The data are sent using UDP protocol on localhost:7872 as AsCII string in order which can be found below.
3) It is important to keep the Windows app as much in sync as possible, ideally holding renderer until HMD_SYNC changes.
4) Windows app have to render value of HMD_SYNC as shade of red (in sRGB colorspace) into top-left corner.

### Optional stereo 3D rendering:
1) To enable 3D rendering create file Z:\tmp\xr\sbs
2) Windows app have to render frame for the left eye into left half of the screen and for the right eye into right half of the screen

### Future compatibility:
1) The XrAPI is still very experimental and may change over time
2) To ensure compatbility content of Z:\tmp\xr\version file
3) If the version is starting with 0.1 then it is compatible
4) New features (backward compatible) are added in versions 0.1.1, 0.1.2, etc.
5) Major API changes (not backward compatible) will be versioned as 0.2, 0.3, etc.

### XrAPI data specification
* XrAPI 0.1.0 - the first version providing 6DoF tracking in text format over UDP localhost. Provides data in order which is defined [here](https://github.com/lvonasek/WinlatorXR/blob/28b40afaf08bf06b752a7947cc2c13a8b5cb4ae9/app/src/main/java/com/winlator/cmod/XrAPI.java#L48)

* XrAPI 0.1.1 - added direct controller access, on the end of the received UDP packet is a new string containing characters T (for TRUE) and F (for FALSE).

This string represents the controller buttons in order:
```
L_GRIP, L_MENU, L_THUMBSTICK_PRESS, L_THUMBSTICK_LEFT, L_THUMBSTICK_RIGHT, L_THUMBSTICK_UP, L_THUMBSTICK_DOWN, L_TRIGGER, L_X, L_Y,
R_A, R_B, R_GRIP, R_THUMBSTICK_PRESS, R_THUMBSTICK_LEFT, R_THUMBSTICK_RIGHT, R_THUMBSTICK_UP, R_THUMBSTICK_DOWN, R_TRIGGER
```

* XrAPI 0.1.2 - ~~added headset manufacturer name (single string without spaces)~~
* XrAPI 0.1.3 - no new features, improved performance and bug fixes
* XrAPI 0.1.4 - receiving UDP data from app on localhost:7278, containing two floats as ASCII string. The values indicates length in frames how long should controller vibrate (the first value is for left controller and the second for the right one).
* XrAPI 0.1.5 - information about headset is written into Z:\tmp\xr\system (manufacturer, product, Android version, security patch version)

Note that headset manufacturer added in XrAPI 0.1.2 will be removed in XrAPI 0.2.x, use Z:\tmp\xr\system instead.

### Code examples

* [SixDOFinator_MinimalProject](https://github.com/bigelod/SixDOFinator_MinimalProject) - Unity sample implementation (minimal project)
* [SixDOFinator_SampleProject](https://github.com/bigelod/SixDOFinator_SampleProject) - Unity sample implementation (full project)

---

Original README:

<p align="center">
	<img src="logo.png" width="376" height="128" alt="Winlator Logo" />  
</p>

# Winlator

Winlator is an Android application that lets you to run Windows (x86_64) applications with Wine and Box86/Box64.

# Installation

1. Download and install the APK (Winlator_7.1.apk) from [GitHub Releases](https://github.com/brunodev85/winlator/releases)
2. Launch the app and wait for the installation process to finish

----

[![Play on Youtube](https://img.youtube.com/vi/8PKhmT7B3Xo/1.jpg)](https://www.youtube.com/watch?v=8PKhmT7B3Xo)
[![Play on Youtube](https://img.youtube.com/vi/9E4wnKf2OsI/2.jpg)](https://www.youtube.com/watch?v=9E4wnKf2OsI)
[![Play on Youtube](https://img.youtube.com/vi/czEn4uT3Ja8/2.jpg)](https://www.youtube.com/watch?v=czEn4uT3Ja8)
[![Play on Youtube](https://img.youtube.com/vi/eD36nxfT_Z0/2.jpg)](https://www.youtube.com/watch?v=eD36nxfT_Z0)

----

# Useful Tips

- If you are experiencing performance issues, try changing the Box86/Box64 preset in Container Settings -> Advanced Tab.
- For applications that use .NET Framework, try installing Wine Mono found in Start Menu -> System Tools.
- If some older games don't open, try adding the environment variable MESA_EXTENSION_MAX_YEAR=2003 in Container Settings -> Environment Variables.
- Try running the games using the shortcut on the Winlator home screen, there you can define individual settings for each game.
- To speed up the installers, try changing the Box86/Box64 preset to Intermediate in Container Settings -> Advanced Tab.

# Credits and Third-party apps
- Ubuntu RootFs ([Focal Fossa](https://releases.ubuntu.com/focal))
- Wine ([winehq.org](https://www.winehq.org/))
- Box86/Box64 by [ptitseb](https://github.com/ptitSeb)
- PRoot ([proot-me.github.io](https://proot-me.github.io))
- Mesa (Turnip/Zink/VirGL) ([mesa3d.org](https://www.mesa3d.org))
- DXVK ([github.com/doitsujin/dxvk](https://github.com/doitsujin/dxvk))
- VKD3D ([gitlab.winehq.org/wine/vkd3d](https://gitlab.winehq.org/wine/vkd3d))
- D8VK ([github.com/AlpyneDreams/d8vk](https://github.com/AlpyneDreams/d8vk))
- CNC DDraw ([github.com/FunkyFr3sh/cnc-ddraw](https://github.com/FunkyFr3sh/cnc-ddraw))

Many thanks to [ptitSeb](https://github.com/ptitSeb) (Box86/Box64), [Danylo](https://blogs.igalia.com/dpiliaiev/tags/mesa/) (Turnip), [alexvorxx](https://github.com/alexvorxx) (Mods/Tips) and others.

Thank you to all the people who believe in this project.







