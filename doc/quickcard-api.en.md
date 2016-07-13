## Quick Card API

### Brief introduction

Quick cards are an important part of Ticwear 4.0 of Cubic UI. Swiping down on the dial interface, you can access Quick cards, then swipe left or right to access a different quick card. On the Ticwear companion, you can manage the Quick cards available on your watch. Quick cards can be used to store important information. For third-party developers, the Quick card could replace selecting the application in the application list, then clicking to open, thereby providing a more efficient way of access.

Currently, the system has built-in date, settings, weather, pedometer and music control quick cards. Users can also save search results in a quick voice card. Third-party applications only need to implement an App Widget in the application be able to use the Quick cards. For widget technical details, you can refer to Android's official website Widget description.

### Use

In order to create a Quick card, you need to implement an App Widget in the watch application. Ticwear uses `AppWidgetHost` to obtain a third party Widget `RemoteView`. When the system detects the presence of a third-party Widget, it will save the data to the watch-side database and sync it to the phone, but will not take the initiative to load a Quick card. Users can manage Quick cards on the Ticwatch companion, add cards, remove cards, and other operations. After users add a third-party card, the system will display it in the Quick cards.

### Implementation steps

In Github users can view a quick card sample program , specifically to achieve the following:

![enter image description here](http://developer.chumenwenwen.com/assets/img/widget-demo.jpg)

Quick card program is an example Android Studio project, and is a standard Android application. It can be installed directly to the phone or it can be installed via Bluetooth or Wifi debugging to the watch. Because Ticwatch companion will automatically delete apps without a mobile client after connecting, so please install the sample programs after connecting the watch to Ticwear companion. The following will explain step by step Quick card implementation procedure.

* In order to unify the Quick card style, you need to define a widget layout covering the entire screen, and preferably has a circular background. The following code is a simple widget layout file, the root background image is a circular background.

* Widget Provider Declaration in `AndroidManifest`

* New `appwidget_info.xml` file. In this file, `android:updatePeriodMillis` represents that system automatically updates Widget time. `updatePeriodMillis` needs to be greater than 180,000, or 30 minutes. If the set time is less than 30 minutes, the system will not automatically update the Widget. `android:initialLayout` represents Widget layout.

* Implementing `AppWidgetProvider`

* Background service updates through Widget (optional)

For cases requiring frequent updates, you can update Widget Service in the background. If you only need to update part of the controls, you can use `partiallyUpdateAppWidget` methods. Because the watch memory is very limited, you can only update `TextView`, it is not recommended for frequent updates of `ImageView`, otherwise it will lead to overall system performance decrease, and maybe even lead to `OOM`. If you need to dynamically update `ImageView`, you can put the resource file in `res/drawable`, the resource file preferably being less than 50Kb. An update interval greater than 10 minutes is recommended.

