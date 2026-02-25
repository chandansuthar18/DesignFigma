package com.example.designfigma.ui.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.designfigma.ui.screens.AddFriendScreen
import com.example.designfigma.ui.screens.CreateGoalScreen
import com.example.designfigma.ui.screens.EditProfileScreen
import com.example.designfigma.ui.screens.FriendsScreen
import com.example.designfigma.ui.screens.GoalDetailsScreen
import com.example.designfigma.ui.screens.HomeScreen
import com.example.designfigma.ui.screens.InformationScreen
import com.example.designfigma.ui.screens.ProfileScreen
import com.example.designfigma.ui.screens.ViewGoalScreen
import com.example.designfigma.ui.screens.login.LoginScreen
import com.example.designfigma.ui.screens.login.CreateAccountScreen
import com.example.designfigma.ui.screens.login.ForgotPasswordScreen
import com.example.designfigma.ui.screens.login.VerifyEmailScreen
import com.example.designfigma.viewmodel.AuthViewModel
import com.example.designfigma.ui.screens.splash.*

sealed class Screen(val route: String) {
    data object Splash1 : Screen("splash1")
    data object Splash2 : Screen("splash2")
    data object Splash3 : Screen("splash3")
    data object Splash4 : Screen("splash4")
    data object Login : Screen("login")
    data object CreateAccount : Screen("create_account")
    data object Information : Screen("information")
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object EditProfile : Screen("edit_profile")
    data object CreateGoal : Screen("create_goal")
    data object ViewGoal : Screen("view_goal")
    data object ForgotPassword : Screen("forgot_password")
    data object VerifyEmail : Screen("verify_email")
    data object addfriend : Screen("add_friend")
    data object friends : Screen("friends")

    data object GoalDetails : Screen("goal_details?goalId={goalId}&creatorId={creatorId}&creatorName={creatorName}") {
        fun createRoute(goalId: String, creatorId: String, creatorName: String): String {
            return "goal_details?goalId=$goalId&creatorId=$creatorId&creatorName=$creatorName"
        }
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash1.route,
        modifier = modifier
    ) {
        composable(Screen.Splash1.route) {
            SplashScreen1(onNextClick = { navController.navigate(Screen.Splash2.route) })
        }

        composable(Screen.Splash2.route) {
            SplashScreen2(
                onNextClick = { navController.navigate(Screen.Splash3.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Splash3.route) {
            SplashScreen3(
                onNextClick = { navController.navigate(Screen.Splash4.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Splash4.route) {
            SplashScreen4(
                onNextClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash1.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    // Navigate to Home if user already has a profile
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onFirstTimeLogin = {
                    // Navigate to Information Screen if new user
                    navController.navigate(Screen.Information.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) },
                onSignUpClick = { navController.navigate(Screen.CreateAccount.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateAccount.route) {
            CreateAccountScreen(
                viewModel = authViewModel,
                onLoginClick = {
                    val hasLoginInStack =
                        navController.previousBackStackEntry?.destination?.route == Screen.Login.route
                    if (hasLoginInStack) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.CreateAccount.route) { inclusive = true }
                        }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Information.route) {
            InformationScreen(
                onNextClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Information.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onAddGoalClick = { navController.navigate(Screen.CreateGoal.route) },
                onViewGoalClick = { navController.navigate(Screen.ViewGoal.route) },
                onBackClick = { navController.popBackStack() },
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onEditClick = { navController.navigate(Screen.EditProfile.route) },
                onAddFriendClick = { navController.navigate(Screen.addfriend.route) },
                onYourFriendsClick = { navController.navigate(Screen.friends.route) },
                onCreateGoalClick = { navController.navigate(Screen.CreateGoal.route) },
                onLogoutClick = {
                    authViewModel.signOut() // Execute actual Firebase sign out
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CreateGoal.route) {
            CreateGoalScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.ViewGoal.route) {
            ViewGoalScreen(
                onBackClick = { navController.popBackStack() },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onGoalClick = { gId, cId, cName ->
                    navController.navigate(Screen.GoalDetails.createRoute(gId, cId, cName))
                }
            )
        }
        composable(
            route = Screen.GoalDetails.route,
            arguments = listOf(
                navArgument("goalId") { type = NavType.StringType; defaultValue = "" },
                navArgument("creatorId") { type = NavType.StringType; defaultValue = "" },
                navArgument("creatorName") { type = NavType.StringType; defaultValue = "User" }
            )
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId") ?: ""
            val creatorId = backStackEntry.arguments?.getString("creatorId") ?: ""
            val creatorName = backStackEntry.arguments?.getString("creatorName") ?: ""

            GoalDetailsScreen(
                goalId = goalId,
                creatorId = creatorId,
                creatorName = creatorName,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.addfriend.route) {
            AddFriendScreen(
                onBackClick = { navController.popBackStack() })
        }
        composable(Screen.friends.route) {
            FriendsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onSendClick = { navController.navigate(Screen.VerifyEmail.route) }
            )
        }
        composable(Screen.VerifyEmail.route) {
            VerifyEmailScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
