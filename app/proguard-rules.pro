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

# Keep app-specific packages
-keep class com.singhDevs.chezz.models.** { *; }
-keep class com.singhDevs.chezz.data.** { *; }
-keep class com.singhDevs.chezz.utils.** { *; }
-keep class com.singhDevs.chezz.network.** { *; }
-keep class com.singhDevs.chezz.websocket.** { *; }
-keep interface com.singhDevs.chezz.network.** { *; }
-keep interface com.singhDevs.chezz.websocket.** { *; }

# Keep enums in models
-keep enum com.singhDevs.chezz.models.** { *; }

# AndroidX DataStore
-keep class androidx.datastore.*.** { *; }

# Proto DataStore
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }
-dontwarn com.google.protobuf.**

# Google Credentials Manager
-keep class com.google.android.gms.auth.api.credentials.** { *; }

# Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }
-keep class kotlin.coroutines.jvm.internal.BaseContinuationImpl { *; }
-keep class * extends kotlin.coroutines.jvm.internal.SuspendLambda { *; }
-keep class * extends kotlin.coroutines.jvm.internal.ContinuationImpl { *; }
-keepclassmembers class * {
    @kotlin.coroutines.jvm.internal.DebugMetadata *;
}
-keepclassmembers class * implements kotlin.coroutines.Continuation {
    public static final java.lang.Object COROUTINE_SUSPENDED;
}

# Preserve Kotlin metadata and other attributes
-keepattributes KotlinMetadata
-keepattributes *Annotation*,InnerClasses,Signature,RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations

-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int i(...);
    public static int d(...);
    public static int w(...);
    public static int e(...);
    public static int wtf(...);
}