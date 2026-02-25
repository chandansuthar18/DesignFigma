package com.example.designfigma.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.designfigma.model.GoalData
import com.example.designfigma.viewmodel.ViewGoalViewModel

@Composable
fun ViewGoalScreen(
    viewModel: ViewGoalViewModel = viewModel(),
    onBackClick: () -> Unit,
    // FIX: Changed signature to accept goal details for dynamic navigation
    onGoalClick: (String, String, String) -> Unit,
    onProfileClick: () -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val gender by viewModel.userGender.collectAsState()
    val ongoingGoals by viewModel.ongoingGoals.collectAsState()
    val achievedGoals by viewModel.achievedGoals.collectAsState()

    val avatarRes = if (gender.equals("Boy", ignoreCase = true)) R.drawable.boy_avatar else R.drawable.girl_avatar

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // --- Header Section ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hey, $userName!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF444444)
                )
                Text(
                    text = "Welcome Back!",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0))
                    .clickable { onProfileClick() },
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Your Goals",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF555555)
        )

        // --- Goals List ---
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 30.dp)
        ) {
            // Ongoing Goals
            items(ongoingGoals) { goal ->
                OngoingGoalCard(
                    goal = goal,
                    avatar = avatarRes,
                    onAchieveClick = {
                        onGoalClick(goal.id, goal.creatorId, goal.creator)
                    }
                )
            }

            if (achievedGoals.isNotEmpty()) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                        Text(
                            text = " Achieved ",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                    }
                }
            }

            items(achievedGoals) { goal ->
                AchievedGoalCard(
                    goal = goal,
                    avatar = avatarRes,
                    onCardClick = {
                        onGoalClick(goal.id, goal.creatorId, goal.creator)
                    }
                )
            }
        }
    }
}

@Composable
fun OngoingGoalCard(goal: GoalData, avatar: Int, onAchieveClick: () -> Unit) {
    val themeColor = when(goal.category) {
        "Health" -> Color(0xFF40E0D0)
        "Work" -> Color(0xFFFFB74D)
        else -> Color(0xFFC77878)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAchieveClick() } // Navigation triggers on card click
            .border(1.dp, themeColor, RoundedCornerShape(25.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(25.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = avatar),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp).clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "By ${goal.creator}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Due: ${goal.deadline}",
                    color = Color(0xFFD67878),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(text = goal.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Ongoing", color = themeColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onAchieveClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Details", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AchievedGoalCard(goal: GoalData, avatar: Int, onCardClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF9575CD)),
        shape = RoundedCornerShape(25.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = avatar),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Created by ${goal.creator}",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White
                ) {
                    Text(
                        text = "Achieved",
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}