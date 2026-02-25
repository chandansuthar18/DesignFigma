package com.example.designfigma.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.designfigma.R
import com.example.designfigma.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onProfileClick: () -> Unit,
    onAddGoalClick: () -> Unit,
    onViewGoalClick: () -> Unit,
    onBackClick: () -> Boolean
) {
    // Collecting the live state from the ViewModel
    val userProfile by viewModel.userData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD))
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()) // Added scroll for smaller screens
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        // --- Header Section (Reactive) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Hello", fontSize = 28.sp, color = Color.Gray)
                Text(
                    text = "${userProfile.userName}!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF444444)
                )
            }

            Image(
                painter = painterResource(
                    id = if (userProfile.gender == "Boy") R.drawable.boy_avatar else R.drawable.girl_avatar
                ),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
                    .clickable { onProfileClick() },
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Search Bar ---
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search for goals", color = Color.LightGray) },
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFEEEEEE),
                focusedBorderColor = Color(0xFF88D1D1)
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        ChallengeCard()

        Spacer(modifier = Modifier.height(30.dp))

        ActionButton(
            text = "Add A New Goal",
            containerColor = Color(0xFF88D1D1),
            hasIcon = true,
            onClick = onAddGoalClick,
        )

        Spacer(modifier = Modifier.height(16.dp))

        ActionButton(
            text = "Check Your Goals" ,
            containerColor = Color(0xFFD67A7A),
            onClick = onViewGoalClick,
            hasIcon = false,
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(text = "Discover", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item { DiscoverItem("Workout", Color(0xFFE5D1F5), R.drawable.workout_3d) }
            item { DiscoverItem("Education", Color(0xFFFFD8A8), R.drawable.education_3d) }
            item { DiscoverItem("Good Health", Color(0xFFF9B7B7), R.drawable.health_3d) }
        }
    }
}

@Composable
fun ChallengeCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            Column(modifier = Modifier.fillMaxWidth(0.6f)) {
                Surface(
                    color = Color(0xFFF3E5F5),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Workout", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, color = Color.Magenta)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Welcome Back!", fontSize = 14.sp, color = Color.Gray)
                Text("Today's Challenge", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    (1..6).forEach { day ->
                        val isSelected = day < 5
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF88D1D1) else Color.White)
                                .border(
                                    width = if (!isSelected) 1.dp else 0.dp,
                                    color = if (!isSelected) Color(0xFF88D1D1) else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "$day", fontSize = 12.sp, color = if (isSelected) Color.White else Color(0xFF88D1D1))
                        }
                    }
                }
            }
            Image(
                painter = painterResource(id = R.drawable.challenge_character),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 10.dp)
            )
        }
    }
}

@Composable
fun ActionButton(text: String, containerColor: Color, hasIcon: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(60.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            if (hasIcon) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun DiscoverItem(label: String, bgColor: Color, imgRes: Int) {
    Card(
        modifier = Modifier.size(110.dp, 130.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(painterResource(imgRes), contentDescription = null, modifier = Modifier.size(60.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD64D5D))
        }
    }
}