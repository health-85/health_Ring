# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class com.sw.watches.bean.** { *; }
-keep class com.sw.watches.listener.** { *; }
-keep class com.sw.watches.activity.** { *; }
-keep class com.sw.watches.receiver.** { *; }
-keep class com.sw.watches.service.ZhBraceletService{ *; }
-keep class com.sw.watches.util.LogUtil{ *; }
-keep class com.sw.watches.bleUtil.SpDeviceTools{ *; }
-keep class com.sw.watches.bleUtil.ByteToStringUtil{ *; }
-keep class com.sw.watches.bluetooth.ParseWatchesData{ *; }
-keep class com.sw.watches.notification.NotificationUtils{ *; }
-keep class com.sw.watches.application.ZhbraceletApplication{ *; }
-keep class com.sw.watches.notification.NotificationSetting{ *; }
-keep class com.sw.watches.service.NotificationsListenerService{ *; }
-keep class com.sw.watches.bluetooth.SIATCommand{ *; }

-keep class no.nordicsemi.android.dfu.** { *; }
-keep class no.nordicsemi.android.dfu.DfuProgressListenerAdapter** { *; }
