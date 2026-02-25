package com.example.designfigma.ui.screens.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designfigma.ui.theme.*
import com.example.designfigma.viewmodel.AuthResult
import com.example.designfigma.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase // Changed this


@Composable
fun CreateAccountScreen(
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLoginClick: () -> Unit,
    onBackClick: () -> Boolean
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.authResult.collect { result ->
            when (result) {
                is AuthResult.SignUpSuccess -> {
                    Toast.makeText(context, "Account Created Successfully!", Toast.LENGTH_LONG).show()
                    onLoginClick()
                }
                is AuthResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundWhite)) {
        // --- Header ---
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.38f)
                .clip(RoundedCornerShape(bottomStart = 45.dp, bottomEnd = 45.dp))
                .background(CoralHeader)
        ) {
            Text(
                text = "Create\nAccount",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = Color.White,
                lineHeight = 48.sp,
                modifier = Modifier.padding(start = 40.dp, top = 70.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                .align(Alignment.Center).offset(y = 50.dp),
            shape = RoundedCornerShape(35.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 36.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomUnderlinedTextField(value = name, onValueChange = { name = it }, hint = "Name", leadingIcon = Icons.Default.Person)
                Spacer(modifier = Modifier.height(12.dp))
                CustomUnderlinedTextField(value = email, onValueChange = { email = it }, hint = "Email", leadingIcon = Icons.Default.Email)
                Spacer(modifier = Modifier.height(12.dp))
                CustomUnderlinedTextField(
                    value = password, onValueChange = { password = it }, hint = "Password",
                    leadingIcon = Icons.Default.Lock, isPassword = true,
                    isPasswordVisible = passwordVisible, onVisibilityToggle = { passwordVisible = !passwordVisible }
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        } else {
                            // UI tells ViewModel to execute the logic
                            viewModel.signUp(name, email, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonOrange),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Black.copy(alpha = 0.4f))
                    Text("  OR  ", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Black.copy(alpha = 0.4f))
                }

                OutlinedButton(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.5f))
                ) {
                    Text("Log In", fontSize = 18.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun CustomUnderlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onVisibilityToggle: () -> Unit = {}
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = Color.Gray.copy(alpha = 0.5f), fontSize = 14.sp) },
        leadingIcon = {
            Icon(leadingIcon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = onVisibilityToggle) {
//                    Icon(
//                        // Fixed: Restored the Visibility icons
//                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
//                        contentDescription = "Toggle Visibility",
//                        tint = Color.Black.copy(alpha = 0.6f),
//                        modifier = Modifier.size(20.dp)
//                    )
                }
            }
        },
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Black.copy(alpha = 0.3f),
            cursorColor = Color.Red,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}