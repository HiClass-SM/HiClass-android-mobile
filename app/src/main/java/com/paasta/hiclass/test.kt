package com.paasta.hiclass

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.paasta.hiclass.databinding.FragmentMypageBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class test : AppCompatActivity() {

    val Gallery =1

    private  var mBinding: FragmentMypageBinding?=null
    private  val binding get()=mBinding!!

    private var imgUri: Uri? = null
    lateinit var bodyFile: RequestBody
    lateinit var body: MultipartBody.Part

    private lateinit var addImageBtn: ImageView
    private lateinit var newImage: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        addImageBtn = findViewById(R.id.btn_add_image)
        newImage = findViewById(R.id.img_profile)
        addImageBtn.setOnClickListener{
            loadImage()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Gallery){
            imgUri = data?.data
            try{
                var bitmap : Bitmap = MediaStore.Images.Media.getBitmap(contentResolver,imgUri)
                newImage.setImageBitmap(bitmap)
                addProfileImg()
            }catch (e:Exception){

            }
        }else{

        }
    }

    private fun loadImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action= Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent,"Load Picture"),Gallery)
    }

    private fun addProfileImg(){

        val file: File = File(getFullPathFromUri(imgUri))
        Log.d("방 사진1", file.toString())
        bodyFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        Log.d("방 사진2", bodyFile.toString())
        body = MultipartBody.Part.createFormData("image", (LoginActivity.prefs.getString("email","") + ".jpg"), bodyFile)




    }

    // 절대경로 변환
    fun absolutelyPath(path: Uri): String {

        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor = contentResolver.query(path, proj, null, null, null)!!
        var index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c.moveToFirst()

        var result = c.getString(index)

        return result
    }

    fun getFullPathFromUri(fileUri: Uri?): String? {
        var fullPath: String? = null
        val column = "_data"
        var cursor: Cursor? = contentResolver.query(fileUri!!, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            var document_id = cursor.getString(0)
            if (document_id == null) {
                for (i in 0 until cursor.columnCount) {
                    if (column.equals(cursor.getColumnName(i), ignoreCase = true)) {
                        fullPath = cursor.getString(i)
                        break
                    }
                }
            } else {
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
                cursor.close()
                val projection = arrayOf(column)
                try {
                    cursor = contentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Images.Media._ID + " = ? ",
                        arrayOf(document_id),
                        null
                    )
                    if (cursor != null) {
                        cursor.moveToFirst()
                        fullPath = cursor.getString(cursor.getColumnIndexOrThrow(column))
                    }
                } finally {
                    if (cursor != null) cursor.close()
                }
            }
        }
        return fullPath
    }
}