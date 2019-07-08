package com.example.imagescrapper

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.webkit.URLUtil
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*
import java.io.InputStreamReader
import java.net.URL
import java.util.*


class MainActivity : AdapterImage.AdapterCallback, AppCompatActivity() {

    var images = mutableListOf<Image>()
    var imageAdapter: AdapterImage? = null
    var imageRcyclerView:RecyclerView? = null
    var queueRecyclerView:RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val linearLayoutManager = LinearLayoutManager(this)
        val linearLayoutManager1 = LinearLayoutManager(this)

        // Initie les adapters
        imageAdapter = AdapterImage(images, this)
        val queueAdapter = AdapterQueue(Progress.queue)

        // Initie les RecyclerView et on les lie à leur adapter
        imageRcyclerView = findViewById<RecyclerView>(R.id.images_recycler).apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = imageAdapter
        }

        queueRecyclerView = findViewById<RecyclerView>(R.id.queue_recycler).apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager1
            adapter= queueAdapter
        }

        Progress.textProgress = findViewById<TextView>(R.id.progress).apply { text = Progress.percent.toString() + " %"}

        // Ouvre la Download Dialog
        findViewById<Button>(R.id.show_dialog).setOnClickListener{
            val dialog = DialogDownload().apply {
                listener = object : DialogDownload.Listener {
                    // Déclenche le téléchargement
                    override fun download(url: String) {
                        Progress.queue.add(url)
                        queueRecyclerView!!.adapter = AdapterQueue(Progress.queue.map{ it.take(45) + "..."} as MutableList<String> )
                        startDownload()
                    }
                }
            }
            dialog.show(supportFragmentManager, "Download DialogDownload")
        }

        // Ouvre la Exitt Dialog
        findViewById<Button>(R.id.leaveButton).setOnClickListener{
            val dialog = DialogExit().apply {
                // Déclenche la fermeture de l'application
                listener = object : DialogExit.Listener {
                    override fun deleteAndCancel() {
                        images.clear()
                        imageRcyclerView!!.adapter!!.notifyDataSetChanged()
                        Progress!!.textProgress!!.text = "Deleting..."
                        Progress.reset(true)
                        Handler().postDelayed({
                            finish()
                        }, 1500)
                    }
                    override fun cancel() {
                        Progress!!.textProgress!!.text = "Goodbye"
                        Progress.reset(true)
                        Handler().postDelayed({
                            finish()
                        }, 1500)
                    }
                }
            }
            dialog.show(supportFragmentManager, "Exit")
        }

    }

    // Lance le prochain téléchargement
    override fun downloadCallback(){
        if(Progress.isDownloadReady() && Progress.queue.isNotEmpty()){
            startDownload()
        }
    }

    // Lance le téléchargement dans une coroutine
    fun startDownload() = GlobalScope.launch {
        if( Progress.isDownloadReady() && Progress.queue.isNotEmpty() ){
            // Récupère les liens des images
            val fetchedImages = async {getImages(Progress.queue.first()) }.await()
            Progress.totalImageDownloaded = fetchedImages.count()
            images.addAll( 0, fetchedImages )
            // Met à jour l'UI en revenant dans l'UI Thread
            runOnUiThread(Runnable {
                imageRcyclerView!!.adapter = AdapterImage(images, this@MainActivity)
                Progress.queue.removeAt(0)
                queueRecyclerView!!.adapter = AdapterQueue(Progress.queue.map{ it.take(45) + "..."} as MutableList<String> )
            })
        }
    }
    // Parse une page HTML et renvoie les liens des images
    suspend fun getImages(website:String) = withContext(Dispatchers.Default) {
        val url = URL(website)
        val awaitIsr = InputStreamReader(url.openStream())
        val sc = Scanner(awaitIsr)
        val images = mutableListOf<Image>()
        while (sc.hasNextLine()) {
            val line = sc.nextLine()
            if ("<img" in line) {
                val regex = "src=\"(.*?)\"".toRegex()
                val matches = regex.findAll(line)
                var URLs = matches.map { Image(buildURL( website, it.groupValues[1])  , buildURL( website, it.groupValues[1]) )}.toList()
                images.addAll(URLs)
                images.removeAll { it.sourceImage.contains("gif") || it.sourceImage.contains(".svg")}
            }
        }
        sc.close()
        images
    }

    // Crée un lien utilisable si la source ne contient pas d'hostname exemple: "/assets/image.jpeg"
    fun buildURL(website:String, downloadSource:String): String{

        if( !URLUtil.isValidUrl(downloadSource) ){
            val url = URL(website)
            return url.host + downloadSource
        }
        else return downloadSource
    }
}
