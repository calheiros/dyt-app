package com.jeff.dytbr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.io.File

class ExplorerAdapter(var files: ArrayList<File>, var context: Context) : BaseAdapter() {
    private val archives = arrayOf(".tar", ".zip", ".rar", ".7zip")
    private val videos = arrayOf(".mp4", ".mkv", ".webm")
    private val images = arrayOf(".jpeg", ".jpg", ".gif")

    override fun getCount(): Int {
        return files.size
    }

    override fun getItem(position: Int): Any {
        return files[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.explorer_item, parent, false
            )
        val nameTextView = view.findViewById<TextView>(R.id.file_name_textview)
        val imageView = view.findViewById<ImageView>(R.id.explore_imageview)
        val file = files[position]
        nameTextView.text = files[position].name
        val res = getFileIconResId(file)
        imageView.setImageResource(res)

        return view!!
    }

    private fun getFileIconResId(file: File): Int {
        val name = file.name
        if (file.isDirectory) return R.drawable.ic_baseline_folder
        if (isArchive(name)) return R.drawable.ic_baseline_zip
        if (isImage(name)) return R.drawable.ic_baseline_image
        return if (isVideo(name)) R.drawable.ic_baseline_movie else R.drawable.ic_baseline_file
    }

    private fun haveExtension(name: String, extensions: Array<String>): Boolean {
        for (ext in extensions) {
            if (name.endsWith(ext)) return true
        }
        return false
    }

    private fun isVideo(name: String): Boolean {
        return haveExtension(name, videos)
    }

    private fun isImage(name: String): Boolean {
        return haveExtension(name, images)
    }

    private fun isArchive(name: String): Boolean {
        return haveExtension(name, archives)
    }
}