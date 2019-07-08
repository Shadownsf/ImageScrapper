package com.example.imagescrapper


import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.EditText
import android.widget.LinearLayout
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.Button
import kotlinx.android.synthetic.*


class CallUrlDialog: DialogFragment() {

    var listener: Listener? = null
    interface  Listener {
        fun onText(text: String)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)


        val inflater = activity?.layoutInflater

        val view = inflater?.inflate(R.layout.edit_url,null,false)

        builder.setView(view)

        val text = view?.findViewById<EditText>(R.id.editText)

        builder.setPositiveButton("Download images", object:DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                // call api
                val textUrl = text?.getText()
                listener?.onText(textUrl.toString())
            }
        })

        return builder.create()
    }
}