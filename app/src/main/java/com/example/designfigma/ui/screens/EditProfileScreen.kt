package com.example.designfigma.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.designfigma.R
import com.example.designfigma.viewmodel.EditProfileViewModel

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = viewModel(),
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val profile by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        EditProfileBottomShape()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = painterResource(
                        id = if (profile.gender.trim().equals("Boy", ignoreCase = true))
                            R.drawable.boy_avatar
                        else
                            R.drawable.girl_avatar
                    ),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color(0xFF00CFD5), CircleShape)
                        .background(Color.White),
                    contentScale = ContentScale.Crop
                )

                // Edit Icon Overlay
                Surface(
                    modifier = Modifier
                        .size(35.dp)
                        .offset(x = (-5).dp, y = (-5).dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Change Photo",
                        tint = Color(0xFF00CFD5),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- Header & Save Button ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Personal Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    TextButton(onClick = {
                        viewModel.saveProfile {
                            Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show()
                            onSaveClick()
                        }
                    }) {
                        Text(
                            text = "Save",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00CFD5)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- Input Fields ---
            UnderlinedInputField(
                value = profile.name,
                onValueChange = { viewModel.updateName(it) },
                label = "Name"
            )

            UnderlinedInputField(
                value = profile.gender,
                onValueChange = { viewModel.updateGender(it) },
                label = "Gender (Boy/Girl)"
            )

            UnderlinedInputField(
                value = profile.age,
                onValueChange = { viewModel.updateAge(it) },
                label = "Age"
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Contact Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            UnderlinedInputField(
                value = profile.contactNumber,
                onValueChange = { viewModel.updateContact(it) },
                label = "Contact Number"
            )

            UnderlinedInputField(
                value = profile.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = "Email"
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun UnderlinedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray, fontSize = 14.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color(0xFF00CFD5),
            unfocusedIndicatorColor = Color.LightGray,
            cursorColor = Color(0xFF00CFD5)
        ),
        singleLine = true
    )
}

@Composable
fun EditProfileBottomShape() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path().apply {
            moveTo(0f, size.height)
            lineTo(size.width, size.height)
            lineTo(size.width, size.height * 0.82f)
            quadraticBezierTo(
                size.width * 0.5f, size.height * 0.72f,
                0f, size.height * 0.82f
            )
            close()
        }
        drawPath(path, color = Color(0xFF88D1D1))
    }
}