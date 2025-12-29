package com.ortin.camerax.presenation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ortin.camerax.navigation.NavigationBarItem
import com.ortin.camerax.navigation.ScreenRoutes
import com.ortin.camerax.presenation.utils.clickableWithoutIndication

@Composable
fun CustomBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    elementsColor: Color = Color.Blue,
    backgroundColor: Color = Color.Transparent,
    selectedElementColor: Color = Color.LightGray
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        NavigationBarItem.Photo,
        NavigationBarItem.Video,
        NavigationBarItem.Gallery
    )

    BottomAppBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = backgroundColor,
        contentPadding = PaddingValues(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceAround
        ) {
            items.forEach {
                Column(
                    modifier = Modifier.clickableWithoutIndication {
                        navController.navigate(it.route) {
                            launchSingleTop = true

                            popUpTo(ScreenRoutes.PHOTO_SCREEN) {
                                inclusive = false
                            }
                        }
                    },
                    verticalArrangement = Arrangement . spacedBy (8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(it.icon),
                        contentDescription = null,
                        tint = if (currentRoute == it.route) selectedElementColor else elementsColor
                    )

                    Text(text = it.title)
                }
            }
        }
    }
}
