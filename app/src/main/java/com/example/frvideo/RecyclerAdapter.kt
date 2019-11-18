package com.example.frvideo

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.video_item.view.*

class RecyclerAdapter(
   val mediaWTList: MutableList<Uri>,
    val context: MainActivity) : RecyclerView.Adapter<VideoHolder>() {
    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        holder.v_thumb.setImageBitmap(bitmapFromUri(position))
    }

    private fun bitmapFromUri(position: Int): Bitmap? {
        val thumb : Bitmap
        val mmd = MediaMetadataRetriever()
        val uri = mediaWTList.get(position)
        mmd.setDataSource (context , uri)
        thumb = mmd.frameAtTime
        return thumb
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        return VideoHolder(LayoutInflater.from(context).inflate(R.layout.video_item , parent , false))
    }

    override fun getItemCount(): Int {
        return mediaWTList.size
    }
}

class VideoHolder (v : View) : RecyclerView.ViewHolder(v), View.OnClickListener {
    override fun onClick(p0: View?) {
    }
    val v_thumb = v.v_thumb
}