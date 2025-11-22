# Add project specific ProGuard rules here.
# Keep AdMob classes
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# Keep Billing classes
-keep class com.android.billingclient.** { *; }
-dontwarn com.android.billingclient.**

# Keep app classes
-keep class com.calmare.app.** { *; }
