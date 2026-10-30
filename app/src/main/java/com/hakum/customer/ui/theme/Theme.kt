package com.hakum.customer.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.hakum.customer.R

val Navy=Color(0xFF020A31)
val Blue=Color(0xFF052699)
val Sky=Color(0xFF9DB4FF)
val Paper=Color(0xFFF1F1ED)
val Gilmer=FontFamily(Font(R.font.gilmer_regular),Font(R.font.gilmer_bold,FontWeight.Bold))
val Benzin=FontFamily(Font(R.font.benzin_semibold,FontWeight.SemiBold))
private val Dark=darkColorScheme(primary=Sky,onPrimary=Navy,background=Navy,onBackground=Paper,surface=Color(0xFF08133F),onSurface=Paper,surfaceVariant=Color(0xFF0D1B52),onSurfaceVariant=Color(0xFF9DA8CD),outline=Color(0xFF24357A),secondary=Sky)
private val Light=lightColorScheme(primary=Blue,onPrimary=Color.White,background=Paper,onBackground=Navy,surface=Color.White,onSurface=Navy,surfaceVariant=Color(0xFFE8EDFF),onSurfaceVariant=Color(0xFF5C6578),outline=Color(0xFFCBD3ED),secondary=Blue)
@Composable fun HakumTheme(dark:Boolean,content:@Composable ()->Unit){
    MaterialTheme(colorScheme=if(dark)Dark else Light,typography=Typography(
        headlineLarge=TextStyle(fontFamily=Benzin,fontSize=28.sp,lineHeight=36.sp,fontWeight=FontWeight.SemiBold,fontStyle=FontStyle.Italic),
        headlineSmall=TextStyle(fontFamily=Benzin,fontSize=20.sp,lineHeight=28.sp,fontWeight=FontWeight.SemiBold),
        titleLarge=TextStyle(fontFamily=Gilmer,fontSize=20.sp,fontWeight=FontWeight.Bold),
        titleMedium=TextStyle(fontFamily=Gilmer,fontSize=16.sp,fontWeight=FontWeight.Bold),
        bodyLarge=TextStyle(fontFamily=Gilmer,fontSize=16.sp,lineHeight=24.sp),
        bodyMedium=TextStyle(fontFamily=Gilmer,fontSize=14.sp,lineHeight=21.sp),
        labelLarge=TextStyle(fontFamily=Gilmer,fontSize=13.sp,fontWeight=FontWeight.Bold),
        labelSmall=TextStyle(fontFamily=Gilmer,fontSize=11.sp,fontWeight=FontWeight.Bold)
    ),content=content)
}
