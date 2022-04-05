package com.paasta.hiclass

import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.widget.Toast
import com.paasta.hiclass.databinding.ActivityEditProfileBinding
import com.paasta.hiclass.databinding.ActivityLoginBinding
import com.paasta.hiclass.databinding.FragmentMypageBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditProfileActivity : AppCompatActivity() {

    private  var mBinding: ActivityEditProfileBinding?=null
    private  val binding get()=mBinding!!

    val Gallery =1

    private var imgUri: Uri? = null
    lateinit var bodyFile: RequestBody
    lateinit var body: MultipartBody.Part

    private var email =""
    private var role =""
    private var oldName =""
    private var oldPassword =""
    private var newName =""
    private var newPassword =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        init()
    }
    private fun init(){
        mBinding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        oldName=LoginActivity.prefs.getString("name","")
        oldPassword=LoginActivity.prefs.getString("password","")
        role =LoginActivity.prefs.getString("role","")
        email =LoginActivity.prefs.getString("email","")

        binding.editName.setHint(oldName)
        addEventListener()

    }
    private fun addEventListener(){

        binding.btnAddImage.setOnClickListener{loadImage()}
//        binding.editNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Gallery){
            imgUri = data?.data
            try{
                var bitmap : Bitmap = MediaStore.Images.Media.getBitmap(contentResolver,imgUri)
                binding.imgProfile.setImageBitmap(bitmap)
//                addProfileImg()
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
        body = MultipartBody.Part.createFormData("image", email, bodyFile)

        RetrofitClient.retrofitservice.requestAddProfileImage(body).enqueue(object :
            Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "통신 실패",
                    Toast.LENGTH_LONG
                ).show()

            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Toast.makeText(
                    applicationContext,
                    "변경 완료",
                    Toast.LENGTH_LONG
                ).show()
                val body = response.body()
                Log.d("변경", body.toString())

            }
        })
    }

    //이미지 절대경로 구하기
    fun getFullPathFromUri(fileUri: Uri?): String? {
        var fullPath: String? = null
        val column = "_data"
        var cursor: Cursor? = contentResolver!!.query(fileUri!!, null, null, null, null)
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
                    cursor = contentResolver!!.query(
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