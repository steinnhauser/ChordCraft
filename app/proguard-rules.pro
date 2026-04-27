# Add project specific ProGuard rules here.
-keep class * implements androidx.viewbinding.ViewBinding {
    *;
}

-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Hilt
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel
