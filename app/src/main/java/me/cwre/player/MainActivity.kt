package me.cwre.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import me.cwre.player.ui.theme.Palette
import me.cwre.player.ui.theme.PaTheme
import me.cwre.player.ui.theme.Space
import me.cwre.player.ui.theme.TypeScale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PaTheme {
                Skeleton()
            }
        }
    }
}

@Composable
private fun Skeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Palette.bg)
            .systemBarsPadding()
            .padding(Space.xl),
    ) {
        Spacer(Modifier.height(Space.xl))
        Text(
            text = "PA Player",
            color = Palette.text1,
            fontSize = TypeScale.display,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(Space.xs))
        Text(
            text = "Personalized × Animation",
            color = Palette.text2,
            fontSize = TypeScale.body,
        )

        Spacer(Modifier.height(Space.xl))

        InfoLine("变体", BuildConfig.VARIANT)
        InfoLine("包名", BuildConfig.APPLICATION_ID)
        InfoLine("版本", BuildConfig.VERSION_NAME)
        InfoLine("versionCode", BuildConfig.VERSION_CODE.toString())
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.padding(vertical = Space.xs),
    ) {
        Text(
            text = label,
            color = Palette.text3,
            fontSize = TypeScale.label,
            modifier = Modifier.padding(end = Space.m),
        )
        Text(
            text = value,
            color = Palette.text1,
            fontSize = TypeScale.label,
        )
    }
}
