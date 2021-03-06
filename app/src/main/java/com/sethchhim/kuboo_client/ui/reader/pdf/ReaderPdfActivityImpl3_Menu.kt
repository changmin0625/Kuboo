package com.sethchhim.kuboo_client.ui.reader.pdf

import android.annotation.SuppressLint
import android.view.MenuItem
import com.sethchhim.kuboo_client.Extensions.setStateDisabled
import com.sethchhim.kuboo_client.Extensions.setStateEnabled
import com.sethchhim.kuboo_client.Settings

@SuppressLint("Registered")
open class ReaderPdfActivityImpl3_Menu : ReaderPdfActivityImpl2_Overlay(){

    protected lateinit var aspectFillMenuItem: MenuItem
    protected lateinit var aspectFitMenuItem: MenuItem
    protected lateinit var fitWidthMenuItem: MenuItem
    protected lateinit var dualPaneMenuItem: MenuItem
    protected lateinit var mangaModeMenuItem: MenuItem
    protected lateinit var localMenuItem: MenuItem

    protected fun toggleDualPaneMode() {
        Settings.DUAL_PANE = !Settings.DUAL_PANE
        sharedPrefsHelper.saveDualPane()
        startActivity(intent)
        updateDualPaneMenuItemState()
    }

    protected fun toggleMangaMode() {
        Settings.RTL = !Settings.RTL
        sharedPrefsHelper.saveRtl()
        startActivity(intent)
        updateMangaModeMenuItemState()
    }


    protected fun updateDualPaneMenuItemState() = when (Settings.DUAL_PANE) {
        true -> dualPaneMenuItem.setStateEnabled(this)
        false -> dualPaneMenuItem.setStateDisabled(this)
    }

    protected fun updateMangaModeMenuItemState() = when (Settings.RTL) {
        true -> mangaModeMenuItem.setStateEnabled(this)
        false -> mangaModeMenuItem.setStateDisabled(this)
    }

}