package com.example.designfigma.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.designfigma.R
import com.example.designfigma.model.UserCard
import com.example.designfigma.viewmodel.AddFriendViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendScreen(
    viewModel: AddFriendViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val users by viewModel.usersList.collectAsState()
    val sentRequests by viewModel.sentRequests.collectAsState()
    val friendsSet by viewModel.friendsSet.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Find Friends", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        if (users.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF88D1D1))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(users) { user ->
                    val isFriend = friendsSet.contains(user.uid)
                    val isSent = sentRequests.contains(user.uid)

                    FriendCard(
                        user = user,
                        isSent = isSent,
                        isFriend = isFriend,
                        onButtonClick = {
                            when {
                                isFriend -> { /* Do nothing, already friends */ }
                                isSent -> viewModel.cancelFriendRequest(user.uid)
                                else -> viewModel.sendFriendRequest(user.uid)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FriendCard(
    user: UserCard,
    isSent: Boolean,
    isFriend: Boolean,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Image(
                painter = painterResource(
                    id = if (user.gender.equals("Boy", ignoreCase = true))
                        R.drawable.boy_avatar else R.drawable.girl_avatar
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // User Info
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.name, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(text = user.gender, fontSize = 13.sp, color = Color.Gray)
            }

            // Action Button
            Button(
                onClick = onButtonClick,
                enabled = !isFriend, // Disable button if already friends
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isFriend -> Color(0xFFF5F5F5)
                        isSent -> Color(0xFFFFEBEE)
                        else -> Color(0xFF88D1D1)
                    },
                    contentColor = when {
                        isFriend -> Color.Gray
                        isSent -> Color.Red
                        else -> Color.White
                    },
                    disabledContainerColor = Color(0xFFF5F5F5),
                    disabledContentColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp),
                border = if (isSent) BorderStroke(1.dp, Color.Red) else null,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (isFriend) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = when {
                        isFriend -> "Friends"
                        isSent -> "Cancel"
                        else -> "Add Friend"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}