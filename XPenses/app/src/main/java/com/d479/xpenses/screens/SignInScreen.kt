package com.d479.xpenses.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.d479.xpenses.signIn.SignInState
import com.d479.xpenses.viewModels.CatViewModel


@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    state: SignInState,
    onSignInClick: () -> Unit,
    viewModel: CatViewModel
) {
    val context = LocalContext.current

    // LaunchedEffect is used to show a toast when there is a sign in error
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onSignInClick) {
            Text("Sign In")
        }
    }

}