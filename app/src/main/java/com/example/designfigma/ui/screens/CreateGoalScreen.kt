package com.example.designfigma.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.designfigma.R
import com.example.designfigma.viewmodel.CreateGoalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    onBackClick: () -> Unit,
    viewModel: CreateGoalViewModel = viewModel()
) {
    var goalName by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val totalGoals by viewModel.totalGoals.collectAsState()

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.15f)
                quadraticBezierTo(
                    size.width * 0.8f, size.height * 0.45f,
                    0f, size.height * 0.25f
                )
                close()
            }
            drawPath(path, color = Color(0xFFC77878))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Surface(
                modifier = Modifier.size(200.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 10.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.goal_character),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(15.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(25.dp))



            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Personal Development\nGoal",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF444444),
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Text(text = "Let’s make a ", fontSize = 15.sp, color = Color(0xFFC77878), fontWeight = FontWeight.Bold)
                Text(text = "Goal together,", fontSize = 15.sp, color = Color(0xFFC77878), fontWeight = FontWeight.Bold)
            }
            Text(text = "Set your Goal with us!", fontSize = 14.sp, color = Color.Gray)

            Surface(
                color = Color(0xFFCB7C7C).copy(alpha = 0.1f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Total Goals Added: $totalGoals",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC77878),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            GoalInputField(
                value = goalName,
                onValueChange = { goalName = it },
                label = "Set a Goal",
                hint = "Goal Name"
            )

            Spacer(modifier = Modifier.height(20.dp))

            GoalInputField(
                value = deadline,
                onValueChange = { deadline = it },
                label = "Deadline",
                hint = "DD/MM/YYYY"
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select Category",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    placeholder = { Text("Health, Finance, Work...", fontSize = 12.sp, color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFFC77878)
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (goalName.isNotEmpty() && deadline.isNotEmpty()) {
                        viewModel.createGoal(goalName, deadline, category) { success ->
                            if (success) {
                                Toast.makeText(context, "Goal added successfully!", Toast.LENGTH_SHORT).show()
                                goalName = ""
                                deadline = ""
                                category = ""
                            } else {
                                Toast.makeText(context, "Failed to save goal", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill in goal and deadline", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Add Goal", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

//            TextButton(
//                onClick = onBackClick,
//                modifier = Modifier.padding(top = 8.dp)
//            ) {
//                Text("View All Goals", color = Color.Gray, fontWeight = FontWeight.Medium)
//            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun GoalInputField(value: String, onValueChange: (String) -> Unit, label: String, hint: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(hint, fontSize = 12.sp, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.LightGray,
                focusedIndicatorColor = Color(0xFFC77878),
                cursorColor = Color(0xFFC77878)
            )
        )
    }
}