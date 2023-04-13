[![Java](https://forthebadge.com/images/badges/made-with-java.svg)](https://github.com/org/Android-SmartWebView)

# Android Smart WebView
This project is developed to help you create Hybrid Android applications with just webview. Hybrid app comes in between webview and native forms, with this project you can embed any existing webpage or setup an Offline HTML/CSS/Javascript based project.

Android Smart WebView gathers all necessary information needed to make any simple app as powerful as a native Android app. This project takes only required data from device to obtain information, including, GPS Location, File Manager, Camera for Processing Images, Custom Dialogues, Notifications and more with clean minimal design.

## Getting Started
These instructions will help you get your Smart WebView copy up and running on your local machine for development and testing purposes.

### Requirement
The project requires minimum Android API 19+ (4.4 KitKat) SDK to test [API 16+ in previous version]. You can use any IDE according to your comfort, I used Android Studio (latest version by the project publish time) for this.

### Test Run
Try rebuilding the project in your programming environment, once you are done fixing any error (incase if one comes up), you'll be ready to look into the project.

### Permissions
You can remove any of the following requests if you do not need them or you can disable any feature using easy setup variables. Currently, these permissions are must for default variables to work properly.
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.VIBRATE" />
```
`INTERNET` permission is required if you are requesting a weburl or webpage.
`WRITE_EXTERNAL_STORAGE` is required for camera photo creation, if you have enabled `ASWP_FUPLOAD` and `ASWP_CAMUPLOAD` to upload image files.

### Easy Setup
Once your project is ready here are some static variables you can change as per your app requirements.

#### Permission variables
```java
static boolean ASWP_JSCRIPT     = true;     //enable JavaScript for webview
static boolean ASWP_FUPLOAD     = true;     //upload file from webview
static boolean ASWP_CAMUPLOAD   = true;     //enable upload from camera for photos
static boolean ASWP_LOCATION    = true;     //track GPS locations
static boolean ASWP_RATINGS     = true;     //show ratings dialog; auto configured, edit method get_rating() for customizations
static boolean ASWP_PBAR        = true;     //show progress bar in app
static boolean ASWP_ZOOM        = false;    //zoom control for webpages view
static boolean ASWP_SFORM       = false;    //save form cache and auto-fill information
static boolean ASWP_OFFLINE     = false;    //whether the loading webpages are offline or online
static boolean ASWP_EXTURL      = true;     //open external url with default browser instead of app webview
```

#### Configuration variables
Complete URL of your website, landing page or local file as (file:///android_res/dir/file.html)
```java
ASWV_URL      = "http://org.infeeds.com/";	//domain, or directory or locating to any root file
```

If file upload enabled, you can define its extention type, default is "\*/\*" for all file types;

Use "image/*" for image types; check file type references on web for custom file type
```java
ASWV_F_TYPE   = "*/*";
```

## Getting GPS Location
If `ASWP_LOCATION = true` then the app will start requesting GPS locations of the device on regular basis and all of the recorded data will be sent to the webpage in form of cookies, with updated live GPS locations.
```java
COOKIE "lat" for latitude
COOKIE "long" for longitude
```

## Author



## License
UPYOG Source Code is open sources under License [UPYOG CODE, COPYRIGHT AND CONTRIBUTION LICENSE TERMS](https://upyog.niua.org/employee/Upyog%20Code%20and%20Copyright%20License_v1.pdf)



**This project on Infeeds - [Android Smart WebView open source to upload files, get GPS locations and more advanced features](https://infeeds.com/d/CODEmgks/25019/android-smart-webview-open-source-upload)**

**A personal note:** `You all must keep up with programming. It's sometimes difficult and sometimes easy but fun afterall, you can create your own world with programming and that's the beauty of it. So, all the best for your next creation.`

[![Profile](https://forthebadge.com/images/badges/built-with-love.svg)](https://github.com/org)
