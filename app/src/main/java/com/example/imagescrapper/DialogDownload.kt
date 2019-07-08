package com.example.imagescrapper

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.EditText


class DialogDownload: DialogFragment() {

    var listener: Listener? = null

    interface Listener {
        fun download(text: String)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Construction de la Download Dialog
        var builder = AlertDialog.Builder(activity)

        val inflater = activity?.layoutInflater

        // Conversion du layout Download Dialog XML en objet manipulable
        val view = inflater?.inflate(R.layout.dialog_download,null,false)

        // Configuration de la Download Dialog
        builder.setView(view)

        val text = view?.findViewById<EditText>(R.id.editText)

        // Configuration du bouton cancel
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{ dialog,id ->
            dialog.cancel()
        })

        // Configuration du bouton Download
        builder.setPositiveButton("Download", DialogInterface.OnClickListener{ dialog,id ->
            val textUrl = text?.getText()
            listener?.download(textUrl.toString())
        })

        // Retourne une DialogDownload crée
        return builder.create()
    }
}