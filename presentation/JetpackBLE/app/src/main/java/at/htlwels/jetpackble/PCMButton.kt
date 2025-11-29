package at.htlwels.jetpackble

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun PCMButton(text: String, id: Int, value: Int, onClick: () -> Unit = {}) {
    var c: Color
    var lc: Color = Color.Transparent

    if(id == value) {
        c = Color(0xFF989A9B)
        lc = Color.Red
    } else {
        c = Color(0xFF313234)
        lc = c
    }

    Column(Modifier.padding(0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Button(
            onClick = { onClick() },
            modifier = Modifier.fillMaxWidth(1.0f).defaultMinSize(minHeight = 50.dp),
            shape = RectangleShape, // makes it rectangular
            colors = ButtonDefaults.buttonColors(containerColor = c)
        )
        {
            Text(modifier = Modifier,
                text = text,
                color = Color.White,
                //fontSize = 28.sp,
                fontFamily = FontFamily.SansSerif // closest to Arial by default
            )
        }
        Box(
            modifier = Modifier
                .height(4.dp)
                .fillMaxWidth()
                .background(lc)
        )
    }
}