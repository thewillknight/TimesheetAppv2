package com.example.timesheetapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.timesheetapp.R
import com.example.timesheetapp.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(currentRoute: String?, navController: NavController) {
    val title = getTitleFromRoute(currentRoute)

    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.capula_short_logo),
                    contentDescription = "Company Logo",
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout"
                    )
                }
            }
        }
    )
}

private fun getTitleFromRoute(route: String?): String {
    return when {
        route == null -> "Timesheet"
        route.startsWith("submitterTimesheets") -> "Submitter Timesheets"
        route.startsWith("approveSummary") -> "Approve Summary"
        route.startsWith("timesheetSummary") -> "Timesheet Summary"
        route == "submit" -> "Submit"
        route == "approve" -> "Approve"
        route == "admin" -> "Admin"
        else -> "Timesheet"
    }
}
