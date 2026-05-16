package be.corentinvanhaeren.sandwix.ui.screens.login

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import be.corentinvanhaeren.sandwix.R
import be.corentinvanhaeren.sandwix.ui.components.AuthContent
import be.corentinvanhaeren.sandwix.ui.components.AuthTextField
import be.corentinvanhaeren.sandwix.ui.components.BrandMark

@Composable
internal fun LoginScreen(
    contentPadding: PaddingValues,
    authUiState: LoginUiState,
    onEmailUpdate: (String) -> Unit,
    onPasswordUpdate: (String) -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
) {
    val isLoading = authUiState.apiState is LoginApiState.Loading

    AuthContent(contentPadding = contentPadding) {
        BrandMark()

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.app_name_display),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = stringResource(R.string.login_welcome),
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(24.dp))

        AuthTextField(
            value = authUiState.email,
            onValueChange = onEmailUpdate,
            label = stringResource(R.string.email_label),
            placeholder = stringResource(R.string.email_placeholder),
            leadingIcon = Icons.Filled.MailOutline,
            keyboardType = KeyboardType.Email,
        )

        Spacer(Modifier.height(12.dp))

        AuthTextField(
            value = authUiState.password,
            onValueChange = onPasswordUpdate,
            label = stringResource(R.string.password_label),
            placeholder = stringResource(R.string.password_placeholder),
            leadingIcon = Icons.Filled.Lock,
            keyboardType = KeyboardType.Password,
            isPassword = true,
        )

        if (authUiState.loginFailed) {
            Spacer(Modifier.height(12.dp))

            Text(
                text = authUiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onLogin,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Text(
                text = if (isLoading) {
                    "Authenticating"
                } else {
                    stringResource(R.string.login_action)
                },
                fontWeight = FontWeight.Bold,
            )
        }

        TextButton(
            onClick = onRegister,
            enabled = !isLoading
        ) {
            Text(stringResource(R.string.register_prompt))
        }
    }
}