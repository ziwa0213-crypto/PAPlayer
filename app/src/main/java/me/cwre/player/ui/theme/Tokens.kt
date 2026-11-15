package me.cwre.player.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 界面里所有色值、间距、圆角、字号、时长都从这里取。
 * 别处出现字面量就算错，改起来永远只有一个地方。
 */

object Palette {
    // 底色由深到浅，靠层级区分，不靠描边和阴影
    val bg = Color(0xFF000000)
    val surface1 = Color(0xFF0E0E0E)
    val surface2 = Color(0xFF171717)
    val surface3 = Color(0xFF1F1F1F)

    val line = Color(0xFF2A2A2A)

    val text1 = Color(0xFFEDEDED)
    val text2 = Color(0xFF9A9A9A)
    val text3 = Color(0xFF6B6B6B)

    // 主色未定，先用中性白占位。定了只改这一行。
    val accent = Color(0xFFFFFFFF)
    val onAccent = Color(0xFF000000)
}

object Space {
    val xs = 4.dp
    val s = 8.dp
    val m = 12.dp
    val l = 16.dp
    val xl = 24.dp
}

object Radius {
    val sm = 4.dp
    val md = 8.dp
    val lg = 12.dp
    val xl = 16.dp
    val xxl = 20.dp
    val full = 999.dp
}

object TypeScale {
    val caption = 11.sp
    val label = 12.sp
    val body = 14.sp
    val title = 17.sp
    val headline = 20.sp
    val display = 24.sp
}

/** 时长只留三档：快 120 / 常规 220 / 慢 420，单位毫秒。 */
object Motion {
    const val fast = 120
    const val base = 220
    const val slow = 420
}
