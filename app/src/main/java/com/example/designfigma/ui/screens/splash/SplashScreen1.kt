package com.example.designfigma.ui.screens.splash


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designfigma.ui.theme.BrightOrange
import com.example.designfigma.ui.theme.Coral
import com.example.designfigma.ui.theme.LightBg
import com.example.designfigma.ui.theme.Purple
import com.example.designfigma.ui.theme.Teal
import kotlin.io.path.Path
import kotlin.io.path.moveTo

@Composable
fun SplashScreen1(
    onNextClick: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.65f)
                lineTo(size.width * 0.5f, size.height)
                lineTo(0f, size.height * 0.65f)
                close()
            }
            drawPath(path, Coral)
        }

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val path = Path().apply {
                moveTo(0f, size.height * 0.45f)
                lineTo(size.width * 0.4f, size.height * 0.5f)
                quadraticBezierTo(
                    size.width * 0.6f,
                    size.height * 0.55f,
                    size.width * 0.3f,
                    size.height * 0.8f
                )
                lineTo(size.width * 0.2f, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path, Purple)
        }

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCircle(
                color = Teal,
                radius = size.width * 0.55f,
                center = Offset(size.width, size.height)
            )
        }


        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 32.dp, top = 82.dp)
        ) {
            Text(
                text = "Welcome!",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "we are really happy to see you\nhere.",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }


        Button(
            onClick = onNextClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 52.dp, end =52.dp, bottom = 85.dp)
                .height(56.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrightOrange
            )
        ) {
            Text(
                text = "Let's go →",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
    }

