package org.trustnote.wallet.util

import android.widget.ImageView
import android.widget.TextView
import com.google.gson.JsonObject
import org.trustnote.wallet.R
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.wallet.Credential
import org.trustnote.wallet.biz.wallet.PaymentInfo
import org.trustnote.wallet.js.JSApi

object TTTUtils {

    fun toAddressQRText(address: String, amount: Long): String {
        if (amount == 0L) {
            return "${TTT.KEY_TTT_QR_TAG}:$address"
        } else {
            return "${TTT.KEY_TTT_QR_TAG}:$address?amount=$amount"
        }
    }

    fun setupAddressQRCode(address: String, amount: Long, qrImageView: ImageView) {
        val qrForAddress = toAddressQRText(address, amount)
        setupQRCode(qrForAddress, qrImageView)
    }

    fun setupQRCode(qrcode: String, qrImageView: ImageView) {
        val qrWidth = org.trustnote.wallet.TApp.context.resources.getDimension(R.dimen.qr_width).toInt()
        val qrBitmap = AndroidUtils.encodeStrAsQrBitmap(qrcode, qrWidth)
        qrImageView.setImageBitmap(qrBitmap)
    }

    fun parsePaymentFromQRCode(qrCode: String): PaymentInfo {
        val res = PaymentInfo()
        if (qrCode.isNotEmpty() && qrCode.length > 4) {
            val hasAmount = qrCode.contains("?")
            res.receiverAddress = if (hasAmount)
                qrCode.substring(4, qrCode.indexOf('?'))
            else qrCode.substring(4)

            if (hasAmount) {
                val key = "amount"
                try {
                    res.amount = qrCode.substring(qrCode.indexOf(key) + key.length + 1).toLong()
                } catch (e: Exception) {
                    Utils.logW(e.localizedMessage)
                }
            }
        }
        return res
    }

    fun formatMN(view: TextView, amount: Long) {
        view.setText(formatMN(amount))
    }

    fun formatMN(amount: Long): String {
        return "${amount / TTT.w_coinunitValue}.${(amount % TTT.w_coinunitValue).toString().padStart(6, '0').substring(0, 4)}"
    }

    fun isValidAddress(address: String): Boolean {
        //TODO:
        return address.isNotEmpty()
    }

    fun isValidAmount(amount: String, balance: Long): Boolean {
        return amount.isNotEmpty() && amount.toFloat() * TTT.w_coinunitValue <= balance
    }

    fun parseTTTAmount(amountStr: String): Long {
        return (amountStr.toFloat() * TTT.w_coinunitValue).toLong()
    }

    fun formatWalletId(walletId: String): String {
        if (walletId.isEmpty()) {
            return "UNKNOWN"
        }
        if (walletId.length <= 16) {
            return walletId.toUpperCase()
        }
        return """${walletId.substring(0, 8).toUpperCase()}...${walletId.takeLast(8).toUpperCase()}"""
    }


    //Step1: TTT:{"type": "c1","name": "TTT","pub":"xpub6CiT96vM5krNhwFA4ro5nKJ6nq9WykFmAsP18jC1Aa3URb69rvUHw6uvU51MQPkMZQ6BLiC5C1E3Zbsm7Xob3FFhNHJkN3v9xuxfqFFKPP5","n": 0,"v": 1234}
    //    TTT: {
    //        "type": "h1",
    //        "id": "LYnW1wl8qHyHyWjoV2CYOlYhUvE3Gj1jh5tUEFzoMn0=",
    //        "v": 1234
    //    }

    //Setp 3:
    //    TTT: {
    //        "type": "c2",
    //        "addr": "0NEYV3ZCRAJYGJDS5UNN4EOZGNVZJXOLI",
    //        "v": 1234
    //    }
    fun genColdScancodeStep3(myDeviceAddress: String, checkCode: Int): String {
        return """${TTT.KEY_TTT_QR_TAG}:{"type":"c2","addr":"$myDeviceAddress","v":$checkCode}"""
    }

    fun genColdScancodeStep2(jsonObj: JsonObject): String {

        val walletPubKey = jsonObj.getAsJsonPrimitive("pub")?.asString
        val checkCode = jsonObj.getAsJsonPrimitive("v")?.asString
        val walletId = JSApi().walletIDSync(walletPubKey ?: "")

        return """${TTT.KEY_TTT_QR_TAG}:{"type":"h1","id": "$walletId","v":${checkCode ?: ""}}"""
    }

    fun genColdScancodeStep1(credential: Credential): String {
        return """${TTT.KEY_TTT_QR_TAG}:{"type": "c1","name": "${credential.walletName}","pub":"${credential.xPubKey}","n": ${credential.account},"v":${Utils.random.nextInt(8999) + 1000}}"""
    }

}