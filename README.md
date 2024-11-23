# SmartTube Manager

![SmartTube Manager screenshot](/SmartTubeManager.png?raw=true "SmartTube Manager screenshot")

This is an Android application to make it easier to install and uninstall
`SmartTube Beta` arm64-v8a(64bit) and armeabi-v7a(32bit). It primarily to make
downgrading `SmartTube Beta` easier. Given that Android doesn't allow
downgrades, `SmartTube Beta` can't downgrade itself. Also once `SmartTube Beta`
is uninstalled downloading different versions of it on Android devices is
inconvenient.

This application uses the GitHub releases api to pull the list of recent
`SmartTube Beta` versions. It then has the ability to download and install
`SmartTube Beta` apks.

It is **highly** advised that you **backup** your settings from within
`SmartTube Beta` before uninstalling it. This can include your Google accounts
and all your preferences. Once you have reinstalled a version of the
`SmartTube Beta` you can restore your settings.

It has been tested on an Nvidia Shield(arm64-v8a) running Android 11, and a
Pixel phone(arm64-v7a) running Android 15. Feel free to file issues about adding
support for more devices, and versions of Android.

This was inspired by Revanced Manager.

## Installation
1. Pull a URL like 
[1.0.2](https://github.com/edgan/SmartTubeManager/releases/download/1.0.2/smarttubemanager-1.0.2-arm64-v8a.apk)
from the [Releases](https://github.com/edgan/SmartTubeManager/releases)
section on any non-AndroidTV device.
2. Go to
[AFTVnews URL Shortener](https://go.aftvnews.com/) and paste the URL to get a
six digit short code.
3. Install
[Downloader by AFTVnews](https://play.google.com/store/apps/details?id=com.esaba.downloader&hl=en_US&pli=1)
via the [Google Play Store](https://play.google.com/store/) on your AndroidTV
device.
4. Enter the six digit short code to
[Downloader by AFTVnews](https://play.google.com/store/apps/details?id=com.esaba.downloader&hl=en_US&pli=1).
5. Click install.
6. Enable Downloader to install applications.
7. Click install again to install SmartTube Manager.
