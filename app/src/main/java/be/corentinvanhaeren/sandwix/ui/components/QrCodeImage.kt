package be.corentinvanhaeren.sandwix.ui.components

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

fun generateQrCodeBitmap(
    content: String,
    size: Int = 512
): Bitmap {
    val bitMatrix = QRCodeWriter().encode(
        content,
        BarcodeFormat.QR_CODE,
        size,
        size
    )

    val bitmap = Bitmap.createBitmap(
        size,
        size,
        Bitmap.Config.RGB_565
    )

    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(
                x,
                y,
                if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            )
        }
    }

    return bitmap
}

@Composable
fun QrCodeImage(
    afhaalCode: String,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(afhaalCode) {
        generateQrCodeBitmap(afhaalCode)
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "QR-code voor afhaling",
        modifier = modifier.size(260.dp)
    )
}