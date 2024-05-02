package com.waquwex.wordgame_kt.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import java.util.Locale

// Bottom navbar
@Composable
fun NavBar(navRoutes: Array<String>, navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row {
        for (route in navRoutes) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f)
                    .background(Color(0xFF19021a))
                    .border(1.dp, Color.DarkGray)
                    .clickable {
                        if (currentRoute != route) {
                            navController.navigate(route)
                        }
                    }) {
                val routeText =
                    route.split("(?<=[a-z])(?=[A-Z])".toRegex()).joinToString(separator = " ")
                    { it.uppercase(Locale.US) }
                Text(
                    color = Color.White,
                    text = routeText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(4.dp)
                )
            }
        }
    }
}