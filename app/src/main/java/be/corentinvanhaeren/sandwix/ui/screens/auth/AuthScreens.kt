package be.corentinvanhaeren.sandwix.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.ExperimentalMaterial3Api
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

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        onLogin = {},
        onRegister = {}
    )
}

@Composable
internal fun LoginScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    AuthScaffold(title = stringResource(R.string.login_title)) {
        BrandMark()
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.app_name_display), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.login_welcome), style = MaterialTheme.typography.titleMedium)
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
        Button(onClick = onLogin, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text(stringResource(R.string.login_action), fontWeight = FontWeight.Bold)
        }
        TextButton(onClick = onRegister) {
            Text(stringResource(R.string.register_prompt))
        }
    }
}

@Composable

internal fun RegisterScreen(onBack: () -> Unit, onRegister: () -> Unit, onLogin: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    AuthScaffold(title = stringResource(R.string.register_title), onBack = onBack) {
        BrandMark()
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.register_headline), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        AuthTextField(name, { name = it }, stringResource(R.string.name_label), stringResource(R.string.name_placeholder), Icons.Filled.Person)
        Spacer(Modifier.height(10.dp))
        AuthTextField(email, { email = it }, stringResource(R.string.email_label), stringResource(R.string.email_placeholder), Icons.Filled.MailOutline, KeyboardType.Email)
        Spacer(Modifier.height(10.dp))
        AuthTextField(phone, { phone = it }, stringResource(R.string.phone_label), stringResource(R.string.phone_placeholder), Icons.Filled.Phone, KeyboardType.Phone)
        Spacer(Modifier.height(10.dp))
        AuthTextField(password, { password = it }, stringResource(R.string.password_label), stringResource(R.string.password_placeholder), Icons.Filled.Lock, KeyboardType.Password, true)
        Spacer(Modifier.height(10.dp))
        AuthTextField(confirmPassword, { confirmPassword = it }, stringResource(R.string.confirm_password_label), stringResource(R.string.password_placeholder), Icons.Filled.Lock, KeyboardType.Password, true)
        Spacer(Modifier.height(18.dp))
        Button(onClick = onRegister, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text(stringResource(R.string.register_action), fontWeight = FontWeight.Bold)
        }
        TextButton(onClick = onLogin) {
            Text(stringResource(R.string.login_prompt))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable

internal fun AuthScaffold(title: String, onBack: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(topBar = { SandwixTopBar(title, onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
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
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        shape = MaterialTheme.shapes.large,
    )
}
