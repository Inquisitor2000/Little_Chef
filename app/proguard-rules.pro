# ============================================================
# LittleChef ProGuard / R8 rules
# ============================================================

# ============================================================
# Hilt / Dagger
# - Hilt generates components at compile time that R8 cannot
#   see via reflection, but they are accessed reflectively
#   by the Hilt framework at runtime.
# ============================================================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keepclassmembers,allowobfuscation class * {
    @javax.inject.Inject <init>(...);
    @javax.inject.Inject <fields>;
}
-keepclassmembers,allowobfuscation class * {
    @dagger.hilt.android.internal.lifecycle.HiltViewModelMap <fields>;
}
# Keep Hilt-generated components
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# ============================================================
# Room
# - Room entities, DAOs, and Database are accessed reflectively
# - R8 cannot see @Entity/@Dao annotations at runtime if stripped
# ============================================================
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Database class * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# ============================================================
# Kotlin Serialization
# - @Serializable classes need their serializer companions kept
# - R8 strips the generated $$serializer classes otherwise
# ============================================================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keep,includedescriptorclasses class com.littlechef.app.**$$serializer { *; }
-keepclassmembers class com.littlechef.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.littlechef.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers,allowobfuscation class * {
    @kotlinx.serialization.Serializable <fields>;
    @kotlinx.serialization.Serializable <init>(...);
}

# ============================================================
# Ktor HTTP client
# ============================================================
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }

# ============================================================
# Coil image loading
# ============================================================
-keep class coil.** { *; }

# ============================================================
# Compose — keep view classes / R8 doesn't strip these but
# guard against future aggressive optimizations
# ============================================================
-keep class androidx.compose.** { *; }

# ============================================================
# App data models & DI graph nodes — keep all model fields
# so serialization/deserialization works after R8 renaming
# ============================================================
-keepclassmembers class com.littlechef.app.data.** { *; }
-keepclassmembers class com.littlechef.app.domain.model.** { *; }
-keepclassmembers class com.littlechef.app.di.** { *; }

# ============================================================
# Suppress warnings for platform-specific dependencies
# ============================================================
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.jetbrains.annotations.**

# ============================================================
# Strip Log calls in release (R8 assumenosideeffects only works
# when R8 full mode is enabled; also keep error/warn logs)
# ============================================================
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
}
