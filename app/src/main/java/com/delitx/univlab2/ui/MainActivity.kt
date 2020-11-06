package com.delitx.univlab2.ui

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delitx.univlab2.R
import com.delitx.univlab2.parsers.DOMParser
import com.delitx.univlab2.parsers.SAXParser
import com.delitx.univlab2.ui.adapters.SearchAdapter
import com.delitx.univlab2.ui.adapters.SettingsAdapter
import com.delitx.univlab2.view_models.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var mSettingsRecycler: RecyclerView
    private lateinit var mSearchRecycler: RecyclerView
    private lateinit var mSAXButton: RadioButton
    private lateinit var mDOMButton: RadioButton
    private lateinit var mSelectFile: Button
    private lateinit var mSearch: Button
    private lateinit var mMessage: TextView
    private lateinit var mViewModel: MainViewModel
    private val mSettingsAdapter = SettingsAdapter()
    private val mSearchAdapter = SearchAdapter()
    private var mReadFile = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindActivity()
        setupViewModel()
    }

    private fun bindActivity() {
        mSettingsRecycler = findViewById(R.id.search_params)
        mSearchRecycler = findViewById(R.id.recycler)
        mSAXButton = findViewById(R.id.sax_parser)
        mDOMButton = findViewById(R.id.dom_parser)
        mSelectFile = findViewById(R.id.file_select)
        mSearch = findViewById(R.id.search)
        mMessage = findViewById(R.id.message)

        mSearch.isEnabled = false

        mSelectFile.setOnClickListener {
            selectFile()
        }

        mSAXButton.isChecked = true
        mSAXButton.setOnClickListener {
            mDOMButton.isChecked = false
            selectSAX()
        }
        mDOMButton.setOnClickListener {
            mSAXButton.isChecked = false
            selectDOM()
        }

        mSearch.setOnClickListener {
            search()
        }

        mSettingsRecycler.layoutManager = LinearLayoutManager(this)
        mSettingsRecycler.adapter = mSettingsAdapter

        mSearchRecycler.layoutManager = LinearLayoutManager(this)
        mSearchRecycler.adapter = mSearchAdapter
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mViewModel.liveData.observe(this) {
            if (it.isEmpty()) {
                mMessage.setText(R.string.no_items)
            } else {
                mMessage.setText("")
            }
            mSearchAdapter.submitList(it)
        }
        mViewModel.settingsParams.observe(this) {
            mSettingsAdapter.submitList(it)
        }
        mViewModel.error.observe(this){
            if(it){
                showError()
            }
        }
        selectSAX()
    }

    private fun selectFile() {
        mReadFile = (ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
        if (Build.VERSION.SDK_INT >= 23 && !mReadFile) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 10
            )
        }
        if (mReadFile) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("*/*")
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            startActivityForResult(intent, 100)
        }
    }

    private fun search() {
        val conditions = mutableListOf<SettingsAdapter.SettingsPair>()
        val temp = mSettingsAdapter.currentList
        for (i in temp) {
            if (i.value.trim() != "") {
                conditions.add(i)
            }
        }
        mViewModel.search(conditions)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                val mData = data?.data
                val segments = mData?.pathSegments
                if (segments != null) {
                    mViewModel.onFileChoose(contentResolver.openInputStream(mData)!!)
                    mSearch.isEnabled = true
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 10) {
            if (grantResults.isNotEmpty()) {
                var counter = 0
                for (i in 0..permissions.size - 1) {
                    val permission = permissions[i]
                    if (android.Manifest.permission.WRITE_EXTERNAL_STORAGE == permission) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            counter++
                        }
                    }
                }
                if (counter == 1) {
                    mReadFile = true
                    selectFile()
                }
            }
        }
    }

    private fun selectSAX() {
        mViewModel.currentStrategy = SAXParser()
    }

    private fun selectDOM() {
        mViewModel.currentStrategy = DOMParser()
    }
    private fun showError() {
        AlertDialog.Builder(this@MainActivity).setTitle("Error")
            .setPositiveButton("Ok") { dialogInterface: DialogInterface, i: Int ->
                mViewModel.error.value=false
                dialogInterface.dismiss()
            }.setMessage("Your file is incorrect.").create().show()

    }
}