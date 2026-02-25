package com.example.designfigma.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designfigma.R

@Composable
fun VerifyEmailScreen(onBackClick: () -> Unit) {
    // State for the 4 digits
    val otpValues = remember { mutableStateListOf("", "", "", "") }
    val focusRequesters = List(4) { FocusRequester() }

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

        Spacer(modifier = Modifier.height(40.dp))

        // --- Envelope Illustration ---
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
            // Yellow outer circle
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = Color(0xFFFFF1C1)
            ) {}
            // Inner Image (The open envelope icon)
            Image(
                painter = painterResource(id = R.drawable.ic_verify_email), // Replace with your asset
                contentDescription = null,
                modifier = Modifier.size(160.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- Description ---
        Text(
            text = "Please enter the 4 digit code sent\nto farheenqazi436@gmail.com",
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = Color.DarkGray,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- 4 Digit OTP Inputs ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            otpValues.forEachIndexed { index, value ->
                OtpBox(
                    value = value,
                    onValueChange = { newValue ->
                        if (newValue.length <= 1) {
                            otpValues[index] = newValue
                            // Move focus to next box
                            if (newValue.isNotEmpty() && index < 3) {
                                focusRequesters[index + 1].requestFocus()
                            }
                        }
                    },
                    modifier = Modifier.focusRequester(focusRequesters[index])
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Resend Code Text ---
        TextButton(onClick = { /* Logic to resend */ }) {
            Text(
                text = "Resend Code",
                color = Color(0xFFD67A7A), // Coral color
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- Verify Button ---
        Button(
            onClick = { /* Logic to verify OTP */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD67A7A)) // Coral color
        ) {
            Text(
                text = "Verify",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun OtpBox(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .size(60.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF88D1D1),
            unfocusedBorderColor = Color.LightGray
        ),
        singleLine = true
    )
}