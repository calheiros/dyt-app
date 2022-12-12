package com.jeff.dytbr

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ExplorerActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private var currentDir: File? = null
    private var files: ArrayList<File>? = null
    private var explorerListView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explorer)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        explorerListView = findViewById(R.id.explorer_list_view)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermission()
        }
        val file = File(
            Environment.getExternalStorageDirectory().absolutePath
                    + File.separator + "Download"
        )
        openDir(file)
        explorerListView?.onItemClickListener = this
        onBackPressedDispatcher.addCallback(this, object :OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goBack()
            }
        })
    }

    private fun goBack() {
        val parent = currentDir?.parentFile
        if (parent != null && parent.canRead()) {
            openDir(parent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        this.goBack()
        return super.onOptionsItemSelected(item)
    }

    private fun openDir(file: File) {
        if (!file.canRead()) {
            Toast.makeText(this, "Can't read this directory!", Toast.LENGTH_SHORT).show()
            return
        }
        this.currentDir = file
        this.files = getSortedList(file)
        explorerListView?.adapter = ExplorerAdapter(files!!, this)
        supportActionBar?.title = currentDir?.name
    }

    private fun getSortedList(file: File): ArrayList<File> {
        val result = ArrayList<File>()
        var filList = ArrayList<File>()
        var dirList = ArrayList<File>()
        val files = file.listFiles()

        for (f: File in files!!) {
            if (f.isDirectory) {
                dirList.add(f)
            } else {
                filList.add(f)
            }
        }
        dirList = ArrayList(dirList.sorted())
        filList = ArrayList(filList.sorted())

        result.addAll(dirList)
        result.addAll(filList)
        return result
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestPermission() {
        //TODO("Not yet implemented")
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val file = files!![position]

        if (file.isDirectory) {
            val internal = Environment.getExternalStorageDirectory().absolutePath
            val dataDir = "$internal/${DATA_DIR}"
            val prefs: SharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

            if (file.absolutePath.equals(dataDir)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (!prefs.getBoolean("data_access", false)) {
                        getDataDirPermission(dataDir)
                        return
                    } else {
                        Toast.makeText(this, "have data access!", Toast.LENGTH_SHORT).show()
                    }
                }
                Toast.makeText(this,"data dir!\n", Toast.LENGTH_SHORT).show()
                return
            }
            openDir(file)
        } else if(file.name.endsWith(".zip")) {
            openZipFile(file)
        }
    }

    private fun openZipFile(file: File) {
        Toast.makeText(this, "zip file!", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getDataDirPermission(path: String) {
        val file = File(path)
        var startDir = ""
        var finalDirPath = ""

        if (file.exists()) {
                startDir = "Android%2Fdata"
            }

            val sm: StorageManager = getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val intent: Intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
            var uri: Uri? = intent.getParcelableExtra("android.provider.extra.INITIAL_URI")
            var scheme: String = uri.toString()

            scheme = scheme.replace("/root/", "/document/")
            finalDirPath = "$scheme%3A$startDir"
            uri = Uri.parse(finalDirPath)
            intent.putExtra("android.provider.extra.INITIAL_URI", uri)

            Log.d("TAG", "uri: $uri")

            try {
                startActivityForResult(intent, 6)
            } catch (_ :ActivityNotFoundException ) {

            }
    }
    @SuppressLint("WrongConstant")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (data != null) {
                val uri = data.data
                if (uri!!.path!!.endsWith("data")) {
                    Log.d("TAG", "onActivityResult: " + uri.path)
                    val takeFlags: Int = (data.flags
                            and Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        contentResolver.takePersistableUriPermission(uri, takeFlags)
                    }

                    val prefs: SharedPreferences = getSharedPreferences("my_preferences",Context.MODE_PRIVATE)
                    prefs.edit().putBoolean("data_access", true).commit()
                    prefs.edit().putString("data_uri", uri.toString()).commit()
                    // save any boolean in pref if user given the right path so we can use the path
                    // in future and avoid to ask permission more than one time
                    loadDataDir(uri)
                } else {
                    // dialog when user gave wrong path
                    //showWrongPathDialog()
                    Toast.makeText(this, "wrong path", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "data is null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadDataDir(uri: Uri) {

    }

    companion object {
        const val DATA_DIR = "Android/data"
        const val OBB_DIR = "Android/obb"
    }
}