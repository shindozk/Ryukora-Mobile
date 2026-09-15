# ProGuard rules for VideoEditor
-keepattributes *Annotation*
-dontobfuscate
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keepclassmembers public class * extends android.app.Activity {
    public void *(android.os.Bundle);
}