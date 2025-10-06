package com.raya.curro.barcodescanner.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raya.curro.barcodescanner.BarScanState
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.launch

class BarCodeScannerViewModel : ViewModel() {
    private var _barScanState by mutableStateOf<BarScanState>(BarScanState.Ideal)
    val barScanState: BarScanState get() = _barScanState

    fun onBarCodeDetected(barcodes: List<Barcode>) {
        viewModelScope.launch {
            // Only process if we actually detect barcodes and we're in Ideal state
            if (barcodes.isEmpty() || _barScanState !is BarScanState.Ideal) {
                return@launch
            }

            _barScanState = BarScanState.Loading

            barcodes.forEach { barcode ->
                barcode.rawValue?.let { identifier ->
                    try {
                        // Process identifier directly
                        _barScanState = BarScanState.ScanSuccess(
                            identifier = identifier.trim(),
                            format = getBarcodeFormatName(barcode.format)
                        )
                    } catch (e: Exception) {
                        Log.e("BarCodeScanner", "Error processing barcode", e)
                        _barScanState = BarScanState.Error("Error processing barcode: ${e.message}")
                    }
                    return@launch
                }
            }
            // If we get here, no valid barcode value was found
            _barScanState = BarScanState.Ideal
        }
    }

    private fun getBarcodeFormatName(format: Int): String {
        return when (format) {
            Barcode.FORMAT_QR_CODE -> "QR Code"
            Barcode.FORMAT_AZTEC -> "AZTEC"
            Barcode.FORMAT_CODABAR -> "CODABAR"
            Barcode.FORMAT_CODE_39 -> "CODE 39"
            Barcode.FORMAT_CODE_93 -> "CODE 93"
            Barcode.FORMAT_CODE_128 -> "CODE 128"
            Barcode.FORMAT_DATA_MATRIX -> "DATA MATRIX"
            Barcode.FORMAT_EAN_8 -> "EAN 8"
            Barcode.FORMAT_EAN_13 -> "EAN 13"
            Barcode.FORMAT_ITF -> "ITF"
            Barcode.FORMAT_PDF417 -> "PDF417"
            Barcode.FORMAT_UPC_A -> "UPC A"
            Barcode.FORMAT_UPC_E -> "UPC E"
            else -> "Unknown"
        }
    }

    fun resetState() {
        _barScanState = BarScanState.Ideal
    }
}
