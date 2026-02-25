package com.example.designfigma.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designfigma.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.material.icons.outlined.PersonAdd
import com.example.designfigma.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onEditClick: () -> Unit,
    onAddFriendClick: () -> Unit,
    onYourFriendsClick: () -> Unit,
    onCreateGoalClick: () -> Unit
) {
    // Collecting the live data from the ViewModel
    val userProfile by viewModel.userData.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        ProfileBackgroundShapes()

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(110.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Image(
                        painter = painterResource(
                            id = if (userProfile.gender == "Boy") R.drawable.boy_avatar else R.drawable.girl_avatar
                        ),
                        contentDescription = null,
                        modifier = Modifier.padding(4.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth().height(90.dp),
                    shape = RoundedCornerShape(30.dp),
                    color = Color(0xFFFFB74D) // Orange
                ) {
                    Column(
                        modifier = Modifier.padding(start = 20.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = userProfile.name.ifEmpty { "User" },
                            color = Color(0xFF444444),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = userProfile.email,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Menu Items
            Column(modifier = Modifier.padding(horizontal = 40.dp)) {
                ProfileMenuItem(Icons.Outlined.Call, userProfile.contactNumber, Color(0xFF6200EE))
                ProfileMenuItem(Icons.Outlined.Person, userProfile.gender, Color(0xFFF4A7A7))

                ProfileMenuItem(
                    icon = Icons.Outlined.PersonAdd,
                    title = "Add Friend",
                    iconColor = Color(0xFF81C784),
                    onClick = onAddFriendClick
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.AccountBox,
                    title = "Your Friends",
                    iconColor = Color(0xFF88D1D1),
                    onClick = onYourFriendsClick
                )

                ProfileMenuItem(Icons.Outlined.CheckCircle, "Milestones", Color(0xFFE5D1F5))

                ProfileMenuItem(
                    icon = Icons.Outlined.Info,
                    title = "Goals",
                    iconColor = Color(0xFFFFD8A8),
                    onClick = onCreateGoalClick
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.ExitToApp,
                    title = "Log Out",
                    iconColor = Color(0xFFF9B7B7),
                    isLogout = true,
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onLogoutClick()
                    }
                )
            }
        }
    }
}
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    iconColor: Color,
    isLogout: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(30.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            color = if (isLogout) Color.Gray else Color(0xFF555555),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ProfileBackgroundShapes() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Teal Top Left
        val topPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width * 0.7f, 0f)
            quadraticBezierTo(size.width * 0.6f, size.height * 0.2f, 0f, size.height * 0.18f)
            close()
        }
        drawPath(topPath, color = Color(0xFF88D1D1))

        // Purple Bottom Shape
        val bottomPath = Path().apply {
            moveTo(0f, size.height)
            lineTo(size.width, size.height)
            lineTo(size.width, size.height * 0.85f)
            quadraticBezierTo(size.width * 0.5f, size.height * 0.8f, 0f, size.height * 0.92f)
            close()
        }
        drawPath(bottomPath, color = Color(0xFF8E6E96))
    }
}