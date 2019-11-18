package com.example.frvideo

import android.Manifest
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

const val REQUEST_VIDEO_CAPTURE = 101
private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE)
private var mediaControls: MediaController? = null
private lateinit var gridLayoutManager: GridLayoutManager
private lateinit var adapter: RecyclerAdapter
val listOfAllVideos: MutableList<Uri> = mutableListOf()
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gridLayoutManager = GridLayoutManager(this , 7)
        videoRecycler.layoutManager = gridLayoutManager
        adapter = RecyclerAdapter(listOfAllVideos , this@MainActivity)
        videoRecycler.adapter = adapter
        val fab = findViewById<FloatingActionButton>(R.id.shutter)
        fab.setOnClickListener(clickListener)
        ActivityCompat.requestPermissions(
            this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
        )
        if (mediaControls != null) {
            mediaControls = MediaController(this@MainActivity)
            videoView.setMediaController(mediaControls)
        }
        val dir : String = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MOVIES).toString()+"/FRVideo/";
        val sdir = File(dir)
        if (!sdir.mkdirs()) {
            sdir.mkdirs()
        }
        if (listOfAllVideos.size == 0) {
            Toast.makeText(this , "STARTING VIDEO PROBE" , Toast.LENGTH_LONG).show()
            getVideos()
        }
    }

    private val clickListener = View.OnClickListener {
                    Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
                takeVideoIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            val videoUri: Uri? = intent?.data
            videoView.setVideoURI(videoUri)
            videoView.start()
            videoView.requestFocus()
            getVideos()
        }
    }

    fun getVideos () : MutableList<Uri> {
        listOfAllVideos.clear()
        val uriExternal: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor?
        val columnIndexID: Int
        val projection = arrayOf(MediaStore.Video.Media._ID)
        var videoId: Long
        cursor = this@MainActivity.contentResolver.query(uriExternal, projection, null, null, null)
        if (cursor != null) {
            columnIndexID = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            while (cursor.moveToNext()) {
                videoId = cursor.getLong(columnIndexID)
                val uriVideo = Uri.withAppendedPath(uriExternal, "" + videoId)
                listOfAllVideos.add(uriVideo)
            }
            cursor.close()
        }
        return listOfAllVideos
    }
}