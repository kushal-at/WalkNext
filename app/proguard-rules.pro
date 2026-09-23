# Proguard rules for WalkNxt
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/.../proguard-android-optimize.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# Room requires some rules, but they are bundled in room-runtime.
# Keep models if accessed via reflection (not needed typically)
-keep class com.walknxt.app.domain.model.** { *; }

-dontwarn kotlin.coroutines.**
