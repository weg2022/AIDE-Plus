

#扩展接口
-keep class io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface {  
    static <methods>;
    <fields>;
    <methods>;
}

# 反射调用
-keep class io.github.zeroaicy.readclass.classInfo.ClassInfoTest3 {
    <fields>;
    <methods>;
}


#签名模块
#保留注解，内部类，封闭类信息
-keepattributes *Annotation*,InnerClasses,EnclosingMethod,Signature

-keepclasseswithmembers class com.android.apksig.internal.asn1.Asn1DerEncoder* {
    *;
}
-keepclasseswithmembers class com.android.apksig.internal.asn1.Asn1BerParser* {
    *;
}

-keepclasseswithmembers class com.android.apksig.internal.asn1.Asn1Class
-keepclasseswithmembers class com.android.apksig.internal.asn1.Asn1Field

#Asn1Class注解的类
-keepclasseswithmembers @com.android.apksig.internal.asn1.Asn1Class class com.android.apksig.** { *;}
#保留Asn1Field注解的字段
-keepclassmembers class com.android.apksig.** {
    @com.android.apksig.internal.asn1.Asn1Field <fields>;
}

#权限申请库
-keep class com.hjq.permissions.Permission {
    <fields>;
    <methods>;
}


# 重写AIDE底包方法类
-keep class com.aide.** {
    <fields>;
    <methods>;
}

-keep class abcd.** {
    <fields>;
    <methods>;
}

# androidx库
-keep class com.support.** {
    <fields>;
    <methods>;
}
-keep class androidx.** {
    <fields>;
    <methods>;
}

# zeroaicy反射库
-keep class io.github.zeroaicy.util.reflect.** {
    <fields>;
    <methods>;
}
-keep class io.github.zeroaicy.util.** {
    <fields>;
    <methods>;
}

#AIDE底包
-dontwarn abcd.**
-dontwarn com.aide.**
-dontwarn

