package org.trustnote.wallet.biz.me

import android.view.View
import android.webkit.WebView
import android.widget.Button
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.biz.wallet.FragmentWalletBase
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.widget.MnemonicsGridView
import org.trustnote.wallet.widget.MyDialogFragment

class FragmentMeBackupMnemonic : FragmentWalletBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_me_backup_mnemonic
    }

    //TODO: listen the wallet update event.

    lateinit var hideLayout: View
    lateinit var showLayout: View
    lateinit var backupLayout: View
    lateinit var removeBtn: Button

    override fun initFragment(view: View) {

        super.initFragment(view)

        hideLayout = findViewById(R.id.hide_mnemonic_layout)
        showLayout = findViewById(R.id.show_mnemonic_layout)

        backupLayout = findViewById(R.id.mnemonic_backup_layout)

        removeBtn = findViewById(R.id.mnemonic_remove_btn)

        hideLayout.setOnClickListener {
            hideMnemonic(true)
        }

        showLayout.setOnClickListener {
            hideMnemonic(false)
        }

        removeBtn.setOnClickListener {
            MyDialogFragment.showDialog2Btns(activity, R.string.dialog_remove_mnemonic_ask, {
                WalletManager.model.removeMnemonicFromProfile()
                onBackPressed()
            })
        }

        val mnemonicsGrid = findViewById<MnemonicsGridView>(R.id.mnemonics)

        mnemonicsGrid.setMnemonic(WalletManager.model.mProfile.mnemonic, false)

        val webView: WebView = view.findViewById(R.id.backup_warning)
        AndroidUtils.setupWarningWebView(webView, "MNEMONIC_BACKUP")

        hideMnemonic(false)

    }

    private fun hideMnemonic(isHide: Boolean) {
        backupLayout.visibility = if (isHide) View.INVISIBLE else View.VISIBLE
        hideLayout.visibility = if (isHide) View.INVISIBLE else View.VISIBLE
        showLayout.visibility = if (isHide) View.VISIBLE else View.INVISIBLE
        removeBtn.visibility = if (isHide) View.INVISIBLE else View.VISIBLE
    }
}
