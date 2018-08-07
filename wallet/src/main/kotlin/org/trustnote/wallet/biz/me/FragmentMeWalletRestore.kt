package org.trustnote.wallet.biz.me

import android.os.Bundle
import android.view.View
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.FragmentProgressBlocking
import org.trustnote.wallet.biz.init.CWFragmentRestore
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.widget.FragmentDialogInputPwd
import org.trustnote.wallet.widget.MyDialogFragment

class FragmentMeWalletRestore : CWFragmentRestore() {

    override fun initFragment(view: View) {
        fromInitActivity = false
        super.initFragment(view)

        MyDialogFragment.showMsg(activity, R.string.me_restore_warning, isTextAlignLeft = true)

        val title = findViewById<TextView>(R.id.mnemonic_restore_title)
        title.text = activity.getString(R.string.mnemonic_restore_me_title)

    }

    override fun startRestore(isRemove: Boolean, mnemonics: String) {

        val inputPwdDialog = FragmentDialogInputPwd()
        inputPwdDialog.confirmLogic = {

            Prefs.saveUserInFullRestore(true)
            CreateWalletModel.savePassphraseInRam(it)

            WalletManager.initWithMnemonic(it, mnemonics, isRemove)

            showWaitingUI {
                onBackPressed()
            }

        }

        addL2Fragment(inputPwdDialog)

    }

}

