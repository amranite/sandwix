package be.corentinvanhaeren.sandwix.ui.screens.register

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
internal fun RegisterScreen(
    contentPadding: PaddingValues,
    registerUiState: RegisterUiState,
    onNaamUpdate: (String) -> Unit,
    onEmailUpdate: (String) -> Unit,
    onTelefoonnummerUpdate: (String) -> Unit,
    onWachtwoordUpdate: (String) -> Unit,
    onBevestigWachtwoordUpdate: (String) -> Unit,
    onRegister: () -> Unit,
    onLogin: () -> Unit,
) {
    val isLoading = registerUiState.apiState is RegisterApiState.Loading

    AuthContent(contentPadding = contentPadding) {
        BrandMark()

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.register_headline),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(20.dp))

        AuthTextField(
            value = registerUiState.naam,
            onValueChange = onNaamUpdate,
            label = stringResource(R.string.name_label),
            placeholder = stringResource(R.string.name_placeholder),
            leadingIcon = Icons.Filled.Person,
        )

        Spacer(Modifier.height(10.dp))

        AuthTextField(
            value = registerUiState.email,
            onValueChange = onEmailUpdate,
            label = stringResource(R.string.email_label),
            placeholder = stringResource(R.string.email_placeholder),
            leadingIcon = Icons.Filled.MailOutline,
            keyboardType = KeyboardType.Email,
        )

        Spacer(Modifier.height(10.dp))

        AuthTextField(
            value = registerUiState.telefoonnummer,
            onValueChange = onTelefoonnummerUpdate,
            label = stringResource(R.string.phone_label),
            placeholder = stringResource(R.string.phone_placeholder),
            leadingIcon = Icons.Filled.Phone,
            keyboardType = KeyboardType.Phone,
        )

        Spacer(Modifier.height(10.dp))

        AuthTextField(
            value = registerUiState.wachtwoord,
            onValueChange = onWachtwoordUpdate,
            label = stringResource(R.string.password_label),
            placeholder = stringResource(R.string.password_placeholder),
            leadingIcon = Icons.Filled.Lock,
            keyboardType = KeyboardType.Password,
            isPassword = true,
        )

        Spacer(Modifier.height(10.dp))

        AuthTextField(
            value = registerUiState.bevestigWachtwoord,
            onValueChange = onBevestigWachtwoordUpdate,
            label = stringResource(R.string.confirm_password_label),
            placeholder = stringResource(R.string.password_placeholder),
            leadingIcon = Icons.Filled.Lock,
            keyboardType = KeyboardType.Password,
            isPassword = true,
        )

        if (registerUiState.registerFailed) {
            Spacer(Modifier.height(12.dp))

            Text(
                text = registerUiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = onRegister,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Text(
                text = if (isLoading) {
                    "Account aanmaken..."
                } else {
                    stringResource(R.string.register_action)
                },
                fontWeight = FontWeight.Bold,
            )
        }

        TextButton(
            onClick = onLogin,
            enabled = !isLoading
        ) {
            Text(stringResource(R.string.login_prompt))
        }
    }
}