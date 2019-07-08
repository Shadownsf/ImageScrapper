package com.example.imagescrapper

import android.widget.TextView

class Progress {
    companion object{
        var renderedImage = 0
        var textProgress: TextView? = null
        var totalImageDownloaded:Int = 0
        var percent:Int = 0
        var isFirstDownload = true
        var queue = mutableListOf<String>()

        fun isDownloadReady():Boolean {
            if (percent >= 100 || isFirstDownload || (!isFirstDownload && percent == 0)){
                reset(false)
                return true
            }
            else {
                return false
            }
        }

        fun reset(resetToFirstDownload:Boolean){
            percent = 0
            renderedImage = 0
            totalImageDownloaded = 0
            isFirstDownload = resetToFirstDownload
        }
    }
}