import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun HeaderActions(modifier: Modifier = Modifier, profilePictureURL: String?) {
    Row(
        modifier = modifier.padding(16.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        if (profilePictureURL != null) {
            AsyncImage(
                model = profilePictureURL,
                contentDescription = "Avatar",
                modifier = modifier.size(40.dp).clip(CircleShape)
            )

        }
    }

}