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
import androidx.compose.ui.res.stringResource
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
                    contentDescription = stringResource(R.string.content_description_logo),
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
                        contentDescription = stringResource(R.string.content_description_logout)
                    )
                }
            }
        }
    )
}

@Composable
private fun getTitleFromRoute(route: String?): String {
    return when {
        route == null -> stringResource(R.string.title_timesheet)
        route.startsWith("submitterTimesheets") -> stringResource(R.string.title_submitter_timesheets)
        route.startsWith("approveSummary") -> stringResource(R.string.title_approve_summary)
        route.startsWith("timesheetSummary") -> stringResource(R.string.title_timesheet_summary)
        route == "submit" -> stringResource(R.string.title_submit)
        route == "approve" -> stringResource(R.string.title_approve)
        route == "admin" -> stringResource(R.string.title_admin)
        else -> stringResource(R.string.title_timesheet)
    }
}
