package com.example.designfigma.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ForgotPasswordScreen(onBackClick: () -> Unit, onSendClick: () -> Unit) {
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Top Bar ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Gray)
            }
            Text(
                text = "Forgot Password",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        // --- Large Teal Lock Icon ---
        Surface(
            modifier = Modifier.size(200.dp),
            shape = CircleShape,
            color = Color(0xFFE0F2F1) // Very light teal background
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(140.dp),
                    shape = CircleShape,
                    color = Color(0xFF88D1D1) // Main teal color
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(35.dp).fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        // --- Instruction Text ---
        Text(
            text = "Please enter your Email Address to\nreceive a verification code.",
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = Color.DarkGray,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // --- Email Input Field ---
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email", color = Color.LightGray) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.LightGray,
                focusedIndicatorColor = Color(0xFF88D1D1)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(60.dp))

        Button(
            onClick = { onSendClick() }, // Updated to trigger navigation
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D))
        ) {
            Text(
                text = "Send",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}