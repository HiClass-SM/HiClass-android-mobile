package com.paasta.hiclass

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.animation.Animator
import android.annotation.TargetApi
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.PictureCallback
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.paasta.hiclass.databinding.ActivityLoginBinding
import com.paasta.hiclass.databinding.ActivityRoomCameraBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class RoomCamera : AppCompatActivity() {

    private  var mBinding: ActivityRoomCameraBinding?=null
    private  val binding get()=mBinding!!

    var sv_viewFinder: SurfaceView? = null
    var sh_viewFinder: SurfaceHolder? = null
    var camera: Camera? = null
    var myfile: File? = null
    var fos: FileOutputStream? = null
    private var dbemail: String? = null
    private lateinit var body: MultipartBody.Part
    var inProgress = false
    var myimage: File? = null
    var index:String? =null
    var roomname:String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_camera)

        mBinding = ActivityRoomCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sv_viewFinder = findViewById<View>(R.id.sv_viewFinder) as SurfaceView
        sh_viewFinder = sv_viewFinder!!.holder
        sh_viewFinder?.addCallback(surfaceListener)
        sh_viewFinder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        binding.roomCountlottie.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {

                startTakePicture()

                binding.roomCountlottie.visibility = View.GONE
                binding.btnAgain?.setVisibility(View.VISIBLE);
                binding.btnNext?.setVisibility(View.VISIBLE);

            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                binding.btnShutter?.setVisibility(View.GONE);
                binding.btnAgain?.setVisibility(View.GONE);
                binding.btnNext?.setVisibility(View.GONE);
            }
        })

    }
    override fun onStart() {
        super.onStart()
        //????????? ????????? ?????? ?????? ????????????
        val cameraPermission = ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)

        if(cameraPermission != PackageManager.PERMISSION_GRANTED){
            //???????????? ???????????? ?????? ?????? ???????????? ??????
            binding.roomCountlottie.visibility = View.GONE
            binding.btnShutter?.setVisibility(View.GONE);
            binding.btnAgain?.setVisibility(View.GONE);
            binding.btnNext?.setVisibility(View.GONE);
            binding.svViewFinder.setVisibility(View.INVISIBLE);
            requestPermission()
        }else{
            init();
        }

    }

    private fun init() {


        //?????????, ?????????
        index = intent.getStringExtra("index")
        roomname = intent.getStringExtra("classname")

        addEventListener()

    }
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        binding.btnShutter?.setVisibility(View.VISIBLE);
        binding.btnAgain?.setVisibility(View.GONE);

    }

    private fun addEventListener() {
        binding.btnShutter.setOnClickListener{
            binding.btnShutter?.setVisibility(View.GONE);
            binding.btnAgain?.setVisibility(View.VISIBLE);
            // room_sendlottie?.setVisibility(View.VISIBLE);
            startTakePicture()
        }
        binding.btnAgain.setOnClickListener {
            camera!!.startPreview()
            binding.btnShutter?.setVisibility(View.VISIBLE);
            binding.btnAgain?.setVisibility(View.GONE);
            //room_sendlottie?.setVisibility(View.GONE);
        }
        binding.btnNext.setOnClickListener {
            if (myfile != null) {
                //               room_secondrocket_lottie.visibility = View.VISIBLE
                Toast.makeText(
                    applicationContext,
                    "?????? ?????? ??? ?????????.",
                    Toast.LENGTH_LONG
                ).show()
                val a: RequestBody =
                    RequestBody.create(MediaType.parse("image/jpeg"), myfile)
                body = MultipartBody.Part.createFormData(
                    "image",
                    (roomname+"_"+index+"_capture" + ".png"), a
                )
                checkimage(body)
                Log.d("????????????", myfile.toString())
                Log.d("????????????", body.toString())
                Log.d("????????????", roomname+"_"+index+"_capture" + ".png")
            } else {
                Toast.makeText(this, "???????????? ???????????????", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA),
            1000
        )

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) { //??????
                // Toast.makeText(this@RoomCamera, "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                // finish()
                showDialogForPermission("?????? ??????????????? ????????? ????????????????????????.")
            }else {

                binding.svViewFinder.setVisibility(View.VISIBLE);
                binding.roomCountlottie.setVisibility(View.VISIBLE);

                addEventListener()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun showDialogForPermission(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("??????")
        builder.setMessage(msg)
        builder.setCancelable(false)
        builder.setPositiveButton(
            "???"
        ) { dialog, id ->
            requestPermission()
        }
        builder.setNegativeButton(
            "?????????"
        ) { arg0, arg1 ->
            finish()
            //  onBackPressed();
        }
        builder.create().show()
    }

    var surfaceListener: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
            Log.i("1", "sufraceListener ????????? ???????????? ??????")
            val parameters = camera!!.parameters
            parameters.setPreviewSize(width, height)
            camera!!.setDisplayOrientation(90)

            camera!!.startPreview()
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            Log.i("1", "sufraceListener ????????? ??????")
            var int_cameraID = 0
            /* ???????????? ????????? ??? ?????? ??? ?????? ?????????  */
            val numberOfCameras = Camera.getNumberOfCameras()
            val cameraInfo = CameraInfo()
            for (i in 0 until numberOfCameras) {
                Camera.getCameraInfo(i, cameraInfo)

                // ???????????????
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT)
                    int_cameraID = i;
                // ???????????????
                //if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) int_cameraID = i
            }
            camera = Camera.open(int_cameraID)

            try {
                camera?.setPreviewDisplay(sh_viewFinder)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            Log.i("1", "sufraceListener ????????? ??????")
            camera!!.release()
//            camera = null
        }
    }

    fun startTakePicture() {
        if (camera != null && inProgress == false) {
            camera!!.takePicture(null, null, takePicture)
            inProgress = true
        }
    }

    var takePicture = PictureCallback { data, camera ->
        Log.d("1", "=== takePicture ===")
        if (data != null) {
            Log.v("1", "takePicture JPEG ?????? ??????")
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            binding.ivPreview!!.setImageBitmap(bitmap)
            binding.ivPreview!!.setVisibility(View.GONE);

            camera.startPreview()
            inProgress = false
            bytearraytoFile(data)
            camera.stopPreview()

        } else {
            Log.e("1", "takePicture data null")
        }
    }

    fun bytearraytoFile(data:ByteArray) {
        myfile = File(applicationContext.getCacheDir(), "image")
        myfile?.createNewFile()
        try {
            fos = FileOutputStream(myfile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(data)
            fos!!.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    fun checkimage(item: MultipartBody.Part) {

        Log.d("??????", item.toString())
        RetrofitClient.retrofitservice.requestCheckImage(item).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "?????? ??????"+t.message,
                    Toast.LENGTH_LONG
                ).show()
            }
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val body = response.body()
                Log.d("?????? ????????????",body.toString())


                if (body != null) {
                    //?????? ?????? ??????
                    val sucessintent = Intent(applicationContext, FaceRecognitionActivity::class.java)
                    sucessintent.putExtra("result", body)
                    sucessintent.putExtra("roomname", roomname)
                    startActivity(sucessintent)

                }else {
                    Toast.makeText(applicationContext, "??????"+response.body(), Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}