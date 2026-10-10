package id.qrisku.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object QriskuColors {
    val Primary = Color(0xFF2563EB)
    val PrimaryDark = Color(0xFF1D4ED8)
    val PrimarySoft = Color(0xFFDBEAFE)
    val Background = Color(0xFFF8FAFC)
    val Surface = Color.White
    val Text = Color(0xFF0F172A)
    val SecondaryText = Color(0xFF475569)
    val Border = Color(0xFFE2E8F0)
    val Success = Color(0xFF15803D)
    val SuccessSoft = Color(0xFFDCFCE7)
    val Warning = Color(0xFFB45309)
    val WarningSoft = Color(0xFFFEF3C7)
    val Error = Color(0xFFB91C1C)
    val ErrorSoft = Color(0xFFFEE2E2)
}

@Composable
fun QriskuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = QriskuColors.Primary,
            onPrimary = Color.White,
            primaryContainer = QriskuColors.PrimarySoft,
            onPrimaryContainer = QriskuColors.PrimaryDark,
            background = QriskuColors.Background,
            onBackground = QriskuColors.Text,
            surface = QriskuColors.Surface,
            onSurface = QriskuColors.Text,
            onSurfaceVariant = QriskuColors.SecondaryText,
            surfaceVariant = QriskuColors.Background,
            outline = QriskuColors.Border,
            outlineVariant = QriskuColors.Border,
            error = QriskuColors.Error,
            onError = Color.White,
            errorContainer = QriskuColors.ErrorSoft,
            onErrorContainer = QriskuColors.Error
        ),
        typography = Typography(
            headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold,
                fontSize = 34.sp, lineHeight = 42.sp, fontFeatureSettings = "tnum"),
            headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp, lineHeight = 32.sp),
            titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp, lineHeight = 28.sp),
            titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp, lineHeight = 24.sp),
            bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, lineHeight = 24.sp),
            bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 20.sp),
            bodySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 20.sp),
            labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp, lineHeight = 24.sp),
            labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium,
                fontSize = 14.sp, lineHeight = 20.sp),
            labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium,
                fontSize = 14.sp, lineHeight = 20.sp)
        ),
        shapes = Shapes(small = RoundedCornerShape(12.dp), medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(20.dp)),
        content = content
    )
}
