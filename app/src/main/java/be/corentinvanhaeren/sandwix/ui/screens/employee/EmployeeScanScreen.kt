package be.corentinvanhaeren.sandwix.ui.screens.employee

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.corentinvanhaeren.sandwix.ui.components.SandwixEmployeeBottomBar
import be.corentinvanhaeren.sandwix.ui.components.SandwixTopBar
import be.corentinvanhaeren.sandwix.ui.navigation.EmployeeTab
import be.corentinvanhaeren.sandwix.ui.scanner.PortraitQrCaptureActivity
import be.corentinvanhaeren.sandwix.ui.theme.SandwixTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
internal fun EmployeeScanScreen(
    contentPadding: PaddingValues,
    uiState: EmployeeScanUiState,
    onSearchOrder: (String) -> Unit,
) {
    var pickupCode by rememberSaveable {
        mutableStateOf("")
    }

    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
    ) { result ->
        val scannedCode = result.contents

        if (!scannedCode.isNullOrBlank()) {
            pickupCode = scannedCode.trim().uppercase()
            onSearchOrder(scannedCode.trim())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "QR-code scannen",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Scan de afhaalcode van de klant.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Button(
                    onClick = {
                        val options = ScanOptions().apply {
                            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                            setPrompt("Scan de QR-code van de klant")
                            setBeepEnabled(true)
                            setOrientationLocked(true)
                            setCaptureActivity(PortraitQrCaptureActivity::class.java)
                        }

                        scanLauncher.launch(options)
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.QrCodeScanner,
                        contentDescription = null,
                    )

                    Text(
                        text = "START SCAN",
                        modifier = Modifier.padding(start = 8.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Zoek bestelling",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Vul de afhaalcode handmatig in als scannen niet lukt.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                OutlinedTextField(
                    value = pickupCode,
                    onValueChange = {
                        pickupCode = it.uppercase()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Afhaalcode")
                    },
                    placeholder = {
                        Text("bv. 1234 of 60AF93")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        keyboardType = KeyboardType.Text,
                    ),
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                )

                Button(
                    onClick = {
                        onSearchOrder(pickupCode.trim())
                    },
                    enabled = pickupCode.isNotBlank() && !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                ) {
                    Text(
                        text = "ZOEK BESTELLING",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
            Text(
                text = "Bestelling zoeken...",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            )
        }

        if (uiState.errorMessage.isNotBlank()) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EmployeeScanScreenPreview() {
    SandwixTheme(darkTheme = false) {
        Scaffold(
            topBar = {
                SandwixTopBar(title = "Afhaalcode scannen")
            },
            bottomBar = {
                SandwixEmployeeBottomBar(
                    selectedTab = EmployeeTab.Scan,
                    onTabSelected = {},
                )
            },
        ) { innerPadding ->
            EmployeeScanScreen(
                contentPadding = innerPadding,
                uiState = EmployeeScanUiState(),
                onSearchOrder = {},
            )
        }
    }
}
