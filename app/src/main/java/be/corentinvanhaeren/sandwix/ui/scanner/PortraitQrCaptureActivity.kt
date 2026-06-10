package be.corentinvanhaeren.sandwix.ui.scanner

import android.app.Activity
import android.os.Bundle
import be.corentinvanhaeren.sandwix.R
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class PortraitQrCaptureActivity : CaptureActivity() {

    override fun initializeContent(): DecoratedBarcodeView {
        setContentView(R.layout.activity_portrait_qr_capture)

        findViewById<android.view.View>(R.id.closeScannerButton).setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        return findViewById(R.id.zxing_barcode_scanner)
    }
}
