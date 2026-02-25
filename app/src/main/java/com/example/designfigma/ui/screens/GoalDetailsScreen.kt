package com.example.designfigma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.designfigma.viewmodel.GoalDetailsViewModel

@Composable
fun GoalDetailsScreen(
    goalId: String,
    creatorId: String,
    creatorName: String,
    viewModel: GoalDetailsViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Milestones", "Chat")

    // Fetch data when screen opens
    LaunchedEffect(goalId) {
        viewModel.fetchGoalDetails(creatorId, goalId)
    }

    val goalData by viewModel.goal.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- Dynamic Header Section ---
        Column(modifier = Modifier.padding(24.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goalData?.title ?: "Loading...",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF555555)
                    )
                    Text(
                        text = "With $creatorName",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = "Deadline: ${goalData?.deadline ?: ""}",
                    color = Color(0xFFD67A7A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = Color.White,
            shadowElevation = 20.dp
        ) {
            Column {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color.Black,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color.Black
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    color = if (selectedTab == index) Color.Black else Color.LightGray
                                )
                            }
                        )
                    }
                }

                if (selectedTab == 0) {
                    MilestonesList()
                } else {
                    // Chat Placeholder
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Chat with $creatorName coming soon!", color = Color.Gray)
                    }
                }
            }
        }
    }
}
@Composable
fun MilestonesList() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val milestoneData = listOf(
            Triple("Day 1", 1f, Color(0xFF8E6E96)),
            Triple("Day 2", 1f, Color(0xFFC77878)),
            Triple("Day 3", 1f, Color(0xFFFFB74D)),
            Triple("Day 4", 0.5f, Color(0xFFB8860B)),
            Triple("Day 5", 0f, Color.LightGray),
            Triple("Day 6", 0f, Color.LightGray),
            Triple("Day 7", 0f, Color.LightGray),
            Triple("Day 8", 0f, Color.LightGray)
        )

        milestoneData.forEach { (day, progress, color) ->
            MilestoneItem(day, progress, color)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "— Something remaining —", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { /* Add logic */ },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF88D1D1))
        ) {
            Text("Add a new Milestone", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun MilestoneItem(day: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = day, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF555555))
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(10.dp)),
                color = color,
                trackColor = Color(0xFFEEEEEE)
            )
            Spacer(modifier = Modifier.width(12.dp))
            // Logic for Emoji or Percentage
            if (progress == 1f) {
                Text(text = "😊", fontSize = 18.sp)
            } else if (progress > 0f) {
                Text(text = "${(progress * 100).toInt()}%", color = Color(0xFFD67A7A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            } else {
                Text(text = "0%", color = Color(0xFFD67A7A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}