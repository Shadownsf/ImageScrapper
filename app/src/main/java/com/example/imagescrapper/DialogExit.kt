package com.example.imagescrapper

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.CheckBox

class DialogExit: DialogFragment() {

    // Delete Callback
    var listener: Listener? = null

    interface Listener {
        fun deleteAndCancel()
        fun cancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Construction de la Exit Dialog
        val builder = AlertDialog.Builder(activity)

        val inflater = activity?.layoutInflater

        // Conversion du layout Exit Dialog XML en objet manipulable
        val dialogView = inflater?.inflate(R.layout.dialog_exit,null,false)

        // Configuration de la Exit Dialog
        builder.setView(dialogView)

        val checkbox = dialogView?.findViewById<CheckBox>(R.id.checkBoxDeleteImage)

        // Configuration du bouton cancel
        builder.setNegativeButton("CANCEL", DialogInterface.OnClickListener{ dialog,id ->
            dialog.cancel()
        })

        // Configuration du bouton Exit qui enclenche ou pas la suppression des images
        builder.setPositiveButton("LEAVE", DialogInterface.OnClickListener { dialog, id ->
            if (checkbox!!.isChecked) {
                listener?.deleteAndCancel()
            } else
                listener?.cancel()
        })

        return builder.create()
    }
}