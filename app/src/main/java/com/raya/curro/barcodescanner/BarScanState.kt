package com.raya.curro.barcodescanner

sealed interface BarScanState {
    data object Ideal : BarScanState
    data class ScanSuccess(
        val identifier: String,
        val format: String
    ) : BarScanState
    data class Error(val error: String) : BarScanState
    data object Loading : BarScanState
}
