package org.trustnote.wallet.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import org.trustnote.db.TxType
import org.trustnote.wallet.R

open class TMnAmount @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val amountView: TextView
    private val decimalView: TextView
    var amount: Long = 0
    private var showAsSmall = false
    private var txType = TxType.invalid

    init {
        val view = View.inflate(context, R.layout.w_mn_amount, null)
        addView(view)
        amountView = view.findViewById(R.id.amount)
        decimalView = view.findViewById(R.id.decimal)
    }

    fun setupStyle(showAsSmall: Boolean) {
        this.showAsSmall = showAsSmall
        if (showAsSmall) {
            amountView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.mn_amount_s_i).toFloat())
            decimalView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.mn_amount_s_d).toFloat())
        } else {
            amountView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.mn_amount_b_i).toFloat())
            decimalView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.mn_amount_b_d).toFloat())
        }
    }

    fun setupStyle(txType: TxType) {
        this.txType = txType

        when (txType) {
            TxType.sent -> {
                amountView.setTextColor(resources.getColor(R.color.tx_red))
                decimalView.setTextColor(resources.getColor(R.color.tx_red))
            }

            TxType.received -> {
                amountView.setTextColor(resources.getColor(R.color.t_blue))
                decimalView.setTextColor(resources.getColor(R.color.t_blue))
            }

            TxType.moved -> {
                amountView.setTextColor(resources.getColor(R.color.t_f_gray))
                decimalView.setTextColor(resources.getColor(R.color.t_f_gray))
            }
        }
    }

    fun setMnAmount(i: Long) {
        amount = i
        updateUI()
    }

    private fun updateUI() {
        amountView.text = """${if (txType == TxType.received) "+" else if (txType == TxType.sent) "-" else ""}${(amount / 1000000).toString()}."""
        decimalView.text = (amount % 1000000).toString().padEnd(4, '0')

    }

}

