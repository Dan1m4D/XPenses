import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderActions(
    modifier: Modifier = Modifier,
    profilePictureURL: String?,
    bgColor: Color = MaterialTheme.colorScheme.surface,
    onSignOut: () -> Unit
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = "Menu",
                modifier = Modifier.size(30.dp).clickable {
                    onSignOut()
                },
                tint = MaterialTheme.colorScheme.primary
            )
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = modifier
            ) {
                Spacer(modifier = modifier.size(8.dp))
                Box(
                    modifier = modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        modifier = Modifier
                            .size(30.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer(modifier = modifier.size(16.dp))
                AsyncImage(
                    model = profilePictureURL,
                    contentDescription = "avatar",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(shape = CircleShape)
                )
                Spacer(modifier = modifier.size(8.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = bgColor,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp)
    )
}
