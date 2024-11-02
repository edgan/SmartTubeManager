# SmartTube Manager

![SmartTube Manager screenshot](/SmartTubeManager.png?raw=true "SmartTube Manager screenshot")

This is an Android application to make it easier to install and uninstall
SmartTube Beta arm64. It primarily to make downgrading SmartTube Beta easier.
Given that Android doesn't allow downgrades, SmartTube Beta can't downgrade
itself. Also once SmartTube Beta is uninstalled downloading different versions
on Android TV devices is inconvenient.

It uses the GitHub releases api to pull the list of recent versions. It then
has the ability to download and install them.

It is highly advised that you backup your SmartTube Beta settings from within
the application before uninstalling the application. This can include your
Google accounts and all your preferences. Once you have reinstalled a version of
the application you can restore your settings.

It has been tested on an Nvidia Shield running Android 11, and a Pixel phone
running Android 15. Feel free to file issues about adding support for more
devices, and versions of Android.

This was inspired by Revanced Manager.
