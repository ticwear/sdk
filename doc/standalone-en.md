## Our Next Big Step: Standalone Apps

First Mobvoi would love to thank all the Ticwear developers for accompanying and supporting us along our journey to greatness and creativity. Now our Ticwatch has more and more functions with quite a few that are able to work independently on the watch without the phone. Our next big goal is to support more standalone wear apps to make Ticwatch run independently without the phone, by which we are hoping to bring Ticwatch lovers a more seamless, intuitive and independent users experience. We wish all of you could join us and make this happen.

### A watch app can be categorized as one of the following: 
 - Completely Independent: a standalone watch app shall be able to run independently on the watch without the phone app or the mobile device, including actions like downloading, installation and regular usage. 
 - Semi-independent:  a phone app is NOT required and would merely provide optional features that are able to run on the watch independently.
 - Not Independent: the app is dependent on a phone app to work.
 
To mark your app as standalone, you ought to define a new meta-data in the Manifest file in the watch’s apk package. Please follow the instructions below to do so.

```
<application>
...
  <meta-data
    android:name="com.google.android.wearable.standalone"
    android:value="true" />
...
</application>
```
If your watch app is completely independent or semi-independent, set the value of the new meta-data element to true. Otherwise set the value to false. 

When uploading a watch app to our developer platform, please keep the app’s status in line with the content defined in the Manifest file above. 
![standalone](http://developer.chumenwenwen.com/uploads/img/markdown/standalone_en.jpg)

### Why Developing A Standalone App For Us
Currently Ticwatch’s Android users are able to download any kinds of apps from the Mobvoi Store while the iOS users merely have access to standalone apps without the access to most apps in the store. Developing a standalone watch app therefore allows you to grow by gaining both Android and iOS users and helps you enable a more intuitive user experience. 

Please Note：
When uploading a standalone wear app, you only need to upload the watch apk without installing it within the corresponding phone apk as before. If you’ve uploaded a standalone app or watch face before, please upload the updated version of your standalone wear app in the way mentioned above as to avoid any confusions or errors of categorization.
