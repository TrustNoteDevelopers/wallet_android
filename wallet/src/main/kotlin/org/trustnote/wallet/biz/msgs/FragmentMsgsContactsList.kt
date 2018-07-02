package org.trustnote.wallet.biz.msgs

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.uiframework.FragmentBaseForHomePage

class FragmentMsgsContactsList : FragmentBaseForHomePage() {

    val model: MessageModel = MessageModel.instance

    override fun getLayoutId(): Int {
        return R.layout.f_msg_home
    }


    lateinit var recyclerView: RecyclerView
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    override fun initFragment(view: View) {

        isBottomLayerUI = true

        super.initFragment(view)

        recyclerView = mRootView.findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        mSwipeRefreshLayout = mRootView.findViewById(R.id.swiperefresh)

        mSwipeRefreshLayout.setProgressViewOffset(true, -60, 40)

        mSwipeRefreshLayout.setOnRefreshListener {

            model.refreshHomeList()
            mSwipeRefreshLayout.isRefreshing = false
            updateUI()
        }

        model.refreshHomeList()

    }

    override fun onResume() {
        super.onResume()
        listener(model.mMessagesEventCenter)
    }

    override fun getTitle(): String {
        return TApp.getString(R.string.menu_msg)
    }

    override fun updateUI() {
        super.updateUI()

        val a = ContactsAdapter(model.latestHomeList)

        a.itemClickListener = {
            chatWithFriend(it.deviceAddress, activity as ActivityBase)
        }

        recyclerView.adapter = a

        mSwipeRefreshLayout.isRefreshing = model.isRefreshing()

    }

    override fun inflateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_action, menu)
    }



}

