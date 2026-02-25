package com.example.designfigma.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designfigma.R
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun InformationScreen(onNextClick: () -> Unit, onBackClick: () -> Boolean) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // Firebase Instances
    val auth = remember { FirebaseAuth.getInstance() }
    val database = remember { FirebaseDatabase.getInstance().getReference("users") }

    var nickname by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Girl") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        BackgroundBottomShapes()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            Spacer(modifier = Modifier.height(70.dp))

            Text(
                text = "Fill the following\ninformation",
                fontSize = 34.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF555555),
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Gender Selection Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                GenderCard(
                    label = "Girl",
                    imageRes = R.drawable.girl_avatar,
                    isSelected = selectedGender == "Girl",
                    containerColor = Color(0xFFF4A7A7),
                    textColor = Color(0xFFD64D5D),
                    onClick = { selectedGender = "Girl" },
                    modifier = Modifier.weight(1f)
                )
                GenderCard(
                    label = "Boy",
                    imageRes = R.drawable.boy_avatar,
                    isSelected = selectedGender == "Boy",
                    containerColor = Color.White,
                    textColor = Color(0xFF6200EE),
                    onClick = { selectedGender = "Boy" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            CapsuleInputField(nickname, { nickname = it }, "Nickname")
            Spacer(modifier = Modifier.height(16.dp))
            CapsuleInputField(age, { age = it }, "Age")
            Spacer(modifier = Modifier.height(16.dp))
            CapsuleInputField(contact, { contact = it }, "Contact Number")

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val uid = auth.currentUser?.uid
                    if (uid == null) {
                        Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (nickname.isBlank() || age.isBlank() || contact.isBlank()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    } else {
                        isLoading = true

                        // Creating the data map
                        val infoMap = mapOf(
                            "nickname" to nickname,
                            "age" to age,
                            "contact" to contact,
                            "gender" to selectedGender
                        )

                        // Saving to Realtime Database under "users/UID/informationscreen"
                        database.child(uid).child("informationscreen")
                            .setValue(infoMap)
                            .addOnSuccessListener {
                                isLoading = false
                                onNextClick()
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Next", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
// GenderCard, CapsuleInputField, and BackgroundBottomShapes stay exactly the same...
@Composable
fun GenderCard(
    label: String,
    imageRes: Int,
    isSelected: Boolean,
    containerColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val borderColor = if (isSelected) Color(0xFF8E6E96) else Color(0xFFE0E0E0)

    Card(
        modifier = modifier
            .aspectRatio(0.85f) // Slightly taller to accommodate the image
            .clickable { onClick() },
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 18.sp
            )

            // Image Box - Fills the remaining space and aligns image to bottom
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = label,
                    modifier = Modifier
                        .fillMaxWidth(0.85f) // Adjust scale of the character
                        .fillMaxHeight(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun CapsuleInputField(value: String, onValueChange: (String) -> Unit, hint: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = Color.Gray, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(50.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = Color(0xFF88D1D1),
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        )
    )
}

@Composable
fun BackgroundBottomShapes() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Purple Shape (Bottom Left)
        val purplePath = Path().apply {
            moveTo(0f, size.height * 0.65f)
            quadraticBezierTo(
                size.width * 0.4f, size.height * 0.75f,
                size.width * 0.2f, size.height
            )
            lineTo(0f, size.height)
            close()
        }
        drawPath(purplePath, color = Color(0xFF8E6E96))

        // Teal Shape (Bottom Right)
        drawCircle(
            color = Color(0xFF88D1D1),
            radius = size.width * 0.6f,
            center = Offset(size.width * 1.1f, size.height * 0.95f)
        )
    }
}