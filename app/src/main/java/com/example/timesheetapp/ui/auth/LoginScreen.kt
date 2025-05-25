package com.example.timesheetapp.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.timesheetapp.R
import com.example.timesheetapp.navigation.Screen
import com.example.timesheetapp.viewmodel.AuthViewModel
import com.example.timesheetapp.viewmodel.AuthResult

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()
    var errorMessage by remember { mutableStateOf("") }

    val titleText = stringResource(R.string.login_screen_title)
    val emailHint = stringResource(R.string.email_field_hint_text)
    val passwordHint = stringResource(R.string.password_field_hint_text)
    val loginButtonText = stringResource(R.string.login_button_text)
    val signUpButtonText = stringResource(R.string.sign_up_button_text)
    val emptyErrorText = stringResource(R.string.empty_email_or_password_error)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.capula_logo),
            contentDescription = "Company Logo",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(vertical = 16.dp),
            contentScale = ContentScale.Fit
        )

        Text(text = titleText, style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(emailHint) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(passwordHint) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                errorMessage = if (email.isBlank() || password.isBlank()) {
                    emptyErrorText
                } else {
                    authViewModel.login(email, password)
                    ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(loginButtonText)
        }

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }

        TextButton(
            onClick = {
                authViewModel.resetState()
                navController.navigate(Screen.SignUp.route)
            }
        ) {
            Text(signUpButtonText)
        }

        when (authState) {
            is AuthResult.Loading -> CircularProgressIndicator()
            is AuthResult.Error -> Text(
                (authState as AuthResult.Error).message,
                color = MaterialTheme.colorScheme.error
            )
            is AuthResult.Success -> {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
            else -> {}
        }
    }
}

