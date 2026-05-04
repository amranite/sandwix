package be.corentinvanhaeren.sandwix.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.corentinvanhaeren.sandwix.R
import be.corentinvanhaeren.sandwix.ui.components.BrandMark
import be.corentinvanhaeren.sandwix.ui.components.SandwixTopBar
import be.corentinvanhaeren.sandwix.ui.theme.SandwixTheme

@Composable
internal fun LoginScreen(
    contentPadding: PaddingValues,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

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
            value = email,
            onValueChange = { email = it },
            label = stringResource(R.string.email_label),
            placeholder = stringResource(R.string.email_placeholder),
            leadingIcon = Icons.Filled.MailOutline,
            keyboardType = KeyboardType.Email,
        )

        Spacer(Modifier.height(12.dp))

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.password_label),
            placeholder = stringResource(R.string.password_placeholder),
            leadingIcon = Icons.Filled.Lock,
            keyboardType = KeyboardType.Password,
            isPassword = true,
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Text(
                text = stringResource(R.string.login_action),
                fontWeight = FontWeight.Bold,
            )
        }

        TextButton(onClick = onRegister) {
            Text(stringResource(R.string.register_prompt))
        }
    }
}

@Composable
internal fun RegisterScreen(
    contentPadding: PaddingValues,
    onRegister: () -> Unit,
    onLogin: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

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
            value = name,
            onValueChange = { name = it },
            label = stringResource(R.string.name_label),
            placeholder = stringResource(R.string.name_placeholder),
            leadingIcon = Icons.Filled.Person,
        )

        Spacer(Modifier.height(10.dp))

        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(R.string.email_label),
            placeholder = stringResource(R.string.email_placeholder),
            leadingIcon = Icons.Filled.MailOutline,
            keyboardType = KeyboardType.Email,
        )

        Spacer(Modifier.height(10.dp))

        AuthTextField(
            value = phone,
            onValueChange = { phone = it },
            label = stringResource(R.string.phone_label),
            placeholder = stringResource(R.string.phone_placeholder),
            leadingIcon = Icons.Filled.Phone,
            keyboardType = KeyboardType.Phone,
        )

        Spacer(Modifier.height(10.dp))

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.password_label),
            placeholder = stringResource(R.string.password_placeholder),
            leadingIcon = Icons.Filled.Lock,
            keyboardType = KeyboardType.Password,
            isPassword = true,
        )

        Spacer(Modifier.height(10.dp))

        AuthTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = stringResource(R.string.confirm_password_label),
            placeholder = stringResource(R.string.password_placeholder),
            leadingIcon = Icons.Filled.Lock,
            keyboardType = KeyboardType.Password,
            isPassword = true,
        )

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = onRegister,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Text(
                text = stringResource(R.string.register_action),
                fontWeight = FontWeight.Bold,
            )
        }

        TextButton(onClick = onLogin) {
            Text(stringResource(R.string.login_prompt))
        }
    }
}

@Composable
internal fun AuthContent(
    contentPadding: PaddingValues,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}

@Composable
internal fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        placeholder = {
            Text(placeholder)
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        shape = MaterialTheme.shapes.large,
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    SandwixTheme(
        darkTheme = false
    ) {
        Scaffold(
            topBar = {
                SandwixTopBar(
                    title = stringResource(R.string.login_title),
                )
            },
        ) { innerPadding ->
            LoginScreen(
                contentPadding = innerPadding,
                onLogin = {},
                onRegister = {},
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreviewDark() {
    SandwixTheme(
        darkTheme = true
    ) {
        Scaffold(
            topBar = {
                SandwixTopBar(
                    title = stringResource(R.string.login_title),
                )
            },
        ) { innerPadding ->
            LoginScreen(
                contentPadding = innerPadding,
                onLogin = {},
                onRegister = {},
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenPreview() {
    SandwixTheme (
        darkTheme = false
    ){
        Scaffold(
            topBar = {
                SandwixTopBar(
                    title = stringResource(R.string.register_title),
                    onBack = {},
                )
            },
        ) { innerPadding ->
            RegisterScreen(
                contentPadding = innerPadding,
                onRegister = {},
                onLogin = {},
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenPreviewDark() {
    SandwixTheme (
        darkTheme = true
    ){
        Scaffold(
            topBar = {
                SandwixTopBar(
                    title = stringResource(R.string.register_title),
                    onBack = {},
                )
            },
        ) { innerPadding ->
            RegisterScreen(
                contentPadding = innerPadding,
                onRegister = {},
                onLogin = {},
            )
        }
    }
}