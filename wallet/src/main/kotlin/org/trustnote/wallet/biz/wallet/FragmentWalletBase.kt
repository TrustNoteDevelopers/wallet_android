package org.trustnote.wallet.biz.wallet

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.uiframework.FragmentBase

abstract class FragmentWalletBase : FragmentBase() {

    protected val disposables: CompositeDisposable = CompositeDisposable()

    //TODO: empty constructor.
    fun getMyActivity(): ActivityMain {
        return activity as ActivityMain
    }

    override fun onResume() {

        super.onResume()

        val d = WalletManager.mWalletEventCenter.observeOn(AndroidSchedulers.mainThread()).subscribe {
            updateUI()
        }
        disposables.add(d)

    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

}

