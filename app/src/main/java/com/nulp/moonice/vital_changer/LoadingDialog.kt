package com.nulp.moonice.vital_changer

import android.app.Activity
import android.app.AlertDialog
import com.nulp.moonice.R

class LoadingDialog(private val mActivity: Activity) {
    private lateinit var isdialog: AlertDialog
    fun startLoading() {
        /**set View*/
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_item, null)

        /**set Dialog*/
        val builder = AlertDialog.Builder(mActivity, R.style.AlertDialogStyle)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isdialog = builder.create()
        isdialog.show()
    }

    fun isDismiss() {
        isdialog.dismiss()
    }
}