-keepattributes *Annotation*, InnerClasses
#-dontnote kotlinx.serialization.AnnotationsKt
# core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Change here com.yourcompany.yourpackage
-keep,includedescriptorclasses class com.mshdabiola.worker**$$serializer { *; } # <-- change package name to your app's
-keepclassmembers class com.mshdabiola.worker.** { # <-- change package name to your app's
    *** Companion;
}
-keepclasseswithmembers class com.mshdabiola.worker.** { # <-- change package name to your app's
    kotlinx.serialization.KSerializer serializer(...);
}