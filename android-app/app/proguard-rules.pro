# ============================================
# OPTIMUS PLAYER - ProGuard Rules
# ============================================
# Regras de ofuscação para Play Store release
# ============================================

# ============================================
# REGRAS BÁSICAS - NÃO REMOVER
# ============================================

# Keep annotations
-keepattributes *Annotation*

# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable

# Keep generic signatures for reflection
-keepattributes Signature

# Keep exception info
-keepattributes Exceptions

# Keep inner classes
-keepattributes InnerClasses

# Keep enums
-keepattributes Enum

# ============================================
# ANDROID FRAMEWORK
# ============================================

# Keep all View constructors
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep Activity methods
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# Keep onClick methods
-keepclassmembers class * {
    public void *Click*(android.view.View);
}

# Keep Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============================================
# KOTLIN
# ============================================

# Keep Kotlin Metadata
-keep class kotlin.Metadata { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ============================================
# RETROFIT & OKHTTP
# ============================================

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# ============================================
# GSON
# ============================================

# Gson uses generic type information stored in a class file when working with fields
-keepattributes Signature

# Keep all model classes (data classes)
-keep class com.optimus.player.domain.model.** { *; }
-keep class com.optimus.player.data.remote.dto.** { *; }

# Gson specific classes
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# ============================================
# EXOPLAYER
# ============================================

# Keep ExoPlayer classes
-keep class com.google.android.exoplayer2.** { *; }
-dontwarn com.google.android.exoplayer2.**

# Keep media codecs
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# ============================================
# GLIDE
# ============================================

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# ============================================
# ROOM DATABASE
# ============================================

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ============================================
# NAVIGATION COMPONENT
# ============================================

-keepnames class androidx.navigation.fragment.NavHostFragment
-keep class * extends androidx.fragment.app.Fragment{}

# ============================================
# VIEW BINDING & DATA BINDING
# ============================================

# Keep ViewBinding classes
-keep class com.optimus.player.databinding.** { *; }

# ============================================
# OPTIMUS PLAYER ESPECÍFICO
# ============================================

# Keep Application class
-keep class com.optimus.player.OptimusPlayerApp { *; }

# Keep all Activities
-keep class com.optimus.player.ui.** extends androidx.appcompat.app.AppCompatActivity { *; }

# Keep all Fragments
-keep class com.optimus.player.ui.** extends androidx.fragment.app.Fragment { *; }

# Keep ViewModels
-keep class com.optimus.player.ui.** extends androidx.lifecycle.ViewModel { *; }

# Keep Repository classes
-keep class com.optimus.player.data.repository.** { *; }

# Keep UseCase classes
-keep class com.optimus.player.domain.usecase.** { *; }

# Keep Utils (PreferenceManager, DeviceUtils)
-keep class com.optimus.player.utils.** { *; }

# Keep data models (licenças, canais, etc)
-keep class com.optimus.player.domain.model.** { *; }

# ============================================
# REMOVER LOGS EM PRODUÇÃO
# ============================================

# Remove all Log calls
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# ============================================
# OTIMIZAÇÕES
# ============================================

# Enable aggressive optimizations
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization options
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# Allow renaming
-repackageclasses ''
-allowaccessmodification

# ============================================
# WARNINGS A IGNORAR
# ============================================

-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn java.lang.instrument.ClassFileTransformer
-dontwarn sun.misc.SignalHandler
