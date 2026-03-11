package com.kjv_app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kjv_app.R

// Define the custom font family mapping to your res/font files
val LoraFontFamily = FontFamily(
    Font(resId = R.font.lora_regular, weight = FontWeight.Normal),
    Font(resId = R.font.lora_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    // Uncomment the line below if you also imported the bold font file
    // Font(resId = R.font.lora_bold, weight = FontWeight.Bold)
)

// Set of Material typography styles tailored for reading
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = LoraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 28.sp, // Generous line height for easier reading
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = LoraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif, // Keeps UI elements crisp and distinct from scripture
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.sp
    )
)