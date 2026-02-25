package com.example.designfigma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
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
import com.example.designfigma.model.Message
import com.example.designfigma.model.Milestone
import com.example.designfigma.viewmodel.GoalDetailsViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun GoalDetailsScreen(
    goalId: String,
    creatorId: String,
    creatorName: String,
    viewModel: GoalDetailsViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val goalData by viewModel.goal.collectAsState()
    val milestones by viewModel.milestones.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(goalId) {
        viewModel.fetchGoalDetails(creatorId, goalId)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // --- Header Section ---
        Column(modifier = Modifier.padding(24.dp)) {
            TextButton(onClick = onBackClick, contentPadding = PaddingValues(0.dp)) {
                Text("< Back", color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = goalData?.title ?: "Loading...", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF555555))
                    Text(text = "With $creatorName", fontSize = 14.sp, color = Color.Gray)
                }
                Text(text = "Deadline: ${goalData?.deadline ?: "N/A"}", color = Color(0xFFD67A7A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab]), color = Color.Black)
                    }
                ) {
                    listOf("Milestones", "Chat").forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, color = if (selectedTab == index) Color.Black else Color.LightGray) }
                        )
                    }
                }

                when (selectedTab) {
                    0 -> MilestonesList(milestones) { viewModel.toggleCheck(creatorId, goalId, it) }
                    1 -> ChatSection(messages, currentUserId) { viewModel.sendMessage(creatorId, goalId, it) }
                }
            }
        }
    }
}

@Composable
fun ChatSection(messages: List<Message>, currentUserId: String, onSend: (String) -> Unit) {
    var textState by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Auto-scroll to bottom
    LaunchedEffect(messages.size) { scrollState.animateScrollTo(Int.MAX_VALUE) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.weight(1f).verticalScroll(scrollState)) {
            messages.forEach { msg ->
                ChatBubble(msg.text, msg.senderId == currentUserId)
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
            )
            IconButton(onClick = { if (textState.isNotBlank()) { onSend(textState); textState = "" } }) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF88D1D1))
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isMe: Boolean) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart) {
        Surface(
            color = if (isMe) Color(0xFF88D1D1) else Color(0xFFF1F1F1),
            shape = RoundedCornerShape(16.dp).copy(
                bottomEnd = if (isMe) CornerSize(0.dp) else CornerSize(16.dp),
                bottomStart = if (isMe) CornerSize(16.dp) else CornerSize(0.dp)
            )
        ) {
            Text(text = text, modifier = Modifier.padding(12.dp), color = if (isMe) Color.White else Color.Black)
        }
    }
}

@Composable
fun MilestonesList(milestones: List<Milestone>, onToggle: (Milestone) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        milestones.forEach { milestone ->
            MilestoneItem(
                day = "Day ${milestone.dayNumber}",
                userAChecked = milestone.userAChecked,
                userBChecked = milestone.userBChecked,
                onCheckClick = { onToggle(milestone) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (milestones.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.padding(20.dp), color = Color(0xFF88D1D1))
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "— Progress updates automatically —", color = Color.LightGray, fontSize = 12.sp)
    }
}

@Composable
fun MilestoneItem(
    day: String,
    userAChecked: Boolean,
    userBChecked: Boolean,
    onCheckClick: () -> Unit
) {
    val progress = when {
        userAChecked && userBChecked -> 1f
        userAChecked || userBChecked -> 0.5f
        else -> 0f
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = day, fontWeight = FontWeight.Bold, color = Color(0xFF555555))

            IconButton(onClick = onCheckClick) {
                Text(
                    text = if (progress == 1f) "😊" else if (progress == 0.5f) "🤔" else "⬜",
                    fontSize = 22.sp
                )
            }
        }

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(10.dp)),
            color = if (progress == 1f) Color(0xFF88D1D1) else Color(0xFFFFB74D),
            trackColor = Color(0xFFEEEEEE)
        )
    }
}