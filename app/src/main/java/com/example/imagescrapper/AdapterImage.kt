package com.example.imagescrapper

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_image.view.*
import java.lang.Exception

class AdapterImage(private val images: MutableList<Image>, mainActivityContext: Context) :
    RecyclerView.Adapter<AdapterImage.MyViewHolder>() {


    val renderedPosition = mutableListOf(-1)

    val adapterCallback = mainActivityContext as AdapterCallback

    interface AdapterCallback { fun downloadCallback() }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val imageView: View, val progressView:View) : RecyclerView.ViewHolder(imageView){
        val link: TextView = imageView.findViewById(R.id.website)
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AdapterImage.MyViewHolder {
        // Creation de la view Image
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)

        // Creation de la View Progression
        val progressView = LayoutInflater.from(parent.context)
            .inflate(R.layout.progress, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(imageView, progressView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val image = images[position]

        val imageView = holder.imageView

        holder.link.text = image.website

        // Chargement des images et calcule de la progression
        Picasso.get()
            .load(image.sourceImage)
            .fit()
            .into(imageView.icon, object: Callback {
                override  fun onSuccess() {
                    if( !renderedPosition.contains(position) && Progress.totalImageDownloaded > 0 ){
                        Progress.renderedImage += 1
                        Progress.percent = Progress.renderedImage * 100 / Progress.totalImageDownloaded
                        if (Progress.percent <= 100){
                            renderedPosition.add(position)
                            Progress.textProgress!!.text = Progress.percent.toString() + " %"
                            if(Progress.percent >= 100 && Progress.queue.isNotEmpty())
                                adapterCallback.downloadCallback()
                        }
                    }
                }

                override fun onError(e: Exception?) {

                }
            })
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = images.size
}