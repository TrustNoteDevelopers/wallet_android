package org.trustnote.db

import io.reactivex.Observable
import io.reactivex.Single
import org.trustnote.db.dao.UnitsDao
import org.trustnote.db.entity.*
import org.trustnote.wallet.TApp
import org.trustnote.wallet.util.Utils
import java.io.File

@Suppress("UNCHECKED_CAST")
object DbHelper {

    fun saveUnits(units: Array<Units>) {
        getDao().saveUnits(units)
    }

    fun saveUnits(units: Units) {
        getDao().saveUnits(arrayOf(units))
    }

    fun saveWalletMyAddress(listAddress: List<MyAddresses>) = saveWalletMyAddressInternal(listAddress)
    fun saveMyWitnesses(myWitnesses: List<String>) {
        getDao().saveMyWitnesses(myWitnesses.mapToTypedArray {
            val res = MyWitnesses()
            res.address = it as String
            res
        })

    }

    fun getMyWitnesses(): Array<MyWitnesses> = getMyWitnessesInternal()
    fun getAllWalletAddress(walletId: String): Array<MyAddresses> = getAllWalletAddressInternal(walletId)
    fun monitorAddresses(): Observable<Array<MyAddresses>> = monitorAddressesInternal()
    fun monitorUnits(): Observable<Array<Units>> = monitorUnitsInternal()
    fun monitorOutputs(): Observable<Array<Outputs>> = monitorOutputsInternal()

    fun shouldGenerateMoreAddress(walletId: String, isChange: Int): Boolean = shouldGenerateMoreAddressInternal(walletId, isChange)
    fun getMaxAddressIndex(walletId: String, isChange: Int): Int = getMaxAddressIndexInternal(walletId, isChange)
    fun shouldGenerateNextWallet(walletId: String): Boolean = shouldGenerateNextWalletInternal(walletId)

    //Balance and tx history
    fun getBanlance(walletId: String): List<Balance> = getBanlanceInternal(walletId)

    fun fixIsSpentFlag() = getDao().fixIsSpentFlag()

    fun getTxs(walletId: String): List<TxUnits> = getTxsInternal(walletId)

    fun queryAddress(addressList: List<String>) = queryAddressInternal(addressList)

    fun queryAddressByAddresdId(addressId: String) = queryAddressByAddresdIdInternal(addressId)

    fun queryAddressByWalletId(walletId: String) = queryAddressByWalletIdInternal(walletId)
    fun queryOutputAddress(unitId: String, walletId: String): List<TxOutputs> {
        return getDao().queryOutputAddress(unitId, walletId).asList()
    }

    fun queryInputAddresses(unitId: String): Array<String> {
        return getDao().queryInputAddresses(unitId)
    }

    fun queryFundedAddressesByAmount(walletId: String, amount: Long): Array<FundedAddress> {
        return getDao().queryFundedAddressesByAmount(walletId, amount)
    }

    fun queryUtxoByAddress(addressList: List<String>, lastBallMCI: Int): Array<Outputs> {
        return getDao().queryUtxoByAddress(addressList, lastBallMCI)
    }

    fun queryUnusedChangeAddress(walletid: String): Array<MyAddresses> {
        return getDao().queryUnusedChangeAddress(walletid)
    }

    fun dropWalletDB(keyDb: String) {

        Utils.debugLog("${TrustNoteDataBase.TAG}dropWalletDB::$keyDb")

        val path = TApp.context.getDatabasePath("trustnote_$keyDb.db").path
        val dbFile = File(path)
        if (dbFile.exists()) {

            val targetFile = File(TApp.context.getDatabasePath("${Utils.nowTimeAsFileName()}_trustnote_$keyDb.db").path)

            Utils.debugLog("${TrustNoteDataBase.TAG}dropWalletDB::targetFile${targetFile.toString()}")

            dbFile.copyTo(targetFile, true)
            dbFile.delete()
            TrustNoteDataBase.removeDb(keyDb)
        }
    }

}

fun getDao(): UnitsDao {
    return TrustNoteDataBase.getInstance(TApp.context).unitsDao()
}

fun getBanlanceInternal(walletId: String): List<Balance> {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    val res = db.unitsDao().queryBalance(walletId)
    return res.toList()
}

fun getMaxAddressIndexInternal(walletId: String, change: Int): Int {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    val max = db.unitsDao().getMaxAddressIndex(walletId, change)
    return if (max > 0) max + 1 else max
}

fun shouldGenerateNextWalletInternal(walletId: String): Boolean {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return db.unitsDao().shouldGenerateNextWallet(walletId)
}


fun shouldGenerateMoreAddressInternal(walletId: String, isChange: Int): Boolean {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return db.unitsDao().shouldGenerateMoreAddress(walletId, isChange)
}

fun getAllWalletAddressInternal(walletId: String): Array<MyAddresses> {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return db.unitsDao().queryAllWalletAddress(walletId)
}

fun getMyWitnessesInternal(): Array<MyWitnesses> {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return db.unitsDao().queryMyWitnesses()
}

fun saveWalletMyAddressInternal(listAddress: List<MyAddresses>) {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    db.unitsDao().insertMyAddresses(listAddress.toTypedArray())
}

fun monitorAddressesInternal(): Observable<Array<MyAddresses>> {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return Utils.throttleDbEvent(db.unitsDao().monitorAddresses().toObservable(), 3L)
}

fun monitorUnitsInternal(): Observable<Array<Units>> {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return Utils.throttleDbEvent(db.unitsDao().monitorUnits().toObservable(), 3L)
}

fun monitorOutputsInternal(): Observable<Array<Outputs>> {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return Utils.throttleDbEvent(db.unitsDao().monitorOutputs().toObservable(), 3L)
}


inline fun <T, reified R> List<T>.mapToTypedArray(transform: (T) -> R): Array<R> {
    return when (this) {
        is RandomAccess -> Array(size) { index -> transform(this[index]) }
        else -> with(iterator()) { Array(size) { transform(next()) } }
    }
}


fun queryAddressInternal(addressList: List<String>): Array<MyAddresses> {
    return getDao().queryAddress(addressList)
}

fun queryAddressByWalletIdInternal(walletId: String): Array<MyAddresses> {
    return getDao().queryAddressByWalletId(walletId)
}


fun queryAddressByAddresdIdInternal(addressId: String): MyAddresses {

    //How about query with no result.
    val res = getDao().queryAddress(listOf<String>(addressId))
    return res[0]
}

private fun getTxsInternal(walletId: String): List<TxUnits> {
    val dao = TrustNoteDataBase.getInstance(TApp.context).unitsDao()
    return dao.queryTxUnits(walletId).asList()
}
