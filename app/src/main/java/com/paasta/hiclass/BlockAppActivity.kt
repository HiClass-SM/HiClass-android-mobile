package com.paasta.hiclass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.*
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.paasta.hiclass.databinding.ActivityBlockAppBinding
import com.paasta.hiclass.databinding.ActivityLoginBinding

class BlockAppActivity : AppCompatActivity() {

    private  var mBinding: ActivityBlockAppBinding?=null
    private  val binding get()=mBinding!!

    private var roomname:String? = null
    private var dbemail: String? = null
    var result:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        init()
    }
    private fun init(){
        mBinding = ActivityBlockAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbemail = LoginActivity.prefs.getString("email","")
        roomname = intent.getStringExtra("roomname")

        checkAccessibility()
        addEventListener()
    }

    private fun addEventListener() {
        binding.btnNext.setOnClickListener {
            if(result==true) {
                check(dbemail.toString())
                val intent = Intent(applicationContext, SettingCameraActivity::class.java)
                intent.putExtra("check", dbemail)
                intent.putExtra("roomname", roomname)
                startActivity(intent)

            }
            else {
                Toast.makeText(
                    applicationContext,
                    "접근성 허용해주세요",
                    Toast.LENGTH_LONG
                ).show()
                setAccessibilityPermissions()
            }


        }
    }

    private fun checkAccessibility(){

        result= checkAccessibilityPermissions()
        Log.d("접근성확인", result.toString())

        //val roomname = intent.getStringExtra("roomname")

        if(result==false) {
            setAccessibilityPermissions()

        }else {
            binding.imgAndroid.setImageResource(R.drawable.androidok)
            binding.txtState.setText("---------SNS 앱이 차단 되었습니다---------")
        }
    }
    //back 키 누르면 홈으로
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        result = checkAccessibilityPermissions()
        Log.d("접근성확인!!!!", result.toString())
        if(result==true) {
            binding.imgAndroid.setImageResource(R.drawable.androidok)
            binding.txtState.setText("---------SNS 앱이 차단 되었습니다---------")
        }

    }

    fun checkAccessibilityPermissions(): Boolean {
        val accessibilityManager =
            getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val list =
            accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        Log.d("service_test", "size : " + list.size)
        for (i in list.indices) {
            val info = list[i]
            Log.d("service_test", "size : " + info.resolveInfo.serviceInfo.packageName)
            if (info.resolveInfo.serviceInfo.packageName == application.packageName) {
                return true
            }
        }
        return false
    }

    fun setAccessibilityPermissions() {

        val permissionDialog= AlertDialog.Builder(this)
        permissionDialog.setTitle("접근성 권한 설정")
        permissionDialog.setMessage("차단 서비스를 위해 접근성 권한이 필요합니다.")
        permissionDialog.setPositiveButton("허용",
            DialogInterface.OnClickListener { dialog, which ->
                startActivity(Intent(ACTION_ACCESSIBILITY_SETTINGS))   //접근성
                return@OnClickListener
            }).create().show()

    }
    fun check(email: String) {
        RetrofitClient.retrofitservice.requestcheckin(email)
            .enqueue(object :
                retrofit2.Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        "전송 실패" + t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {

                    val body = response.body()
                    Log.d("앱 인증 완료", body.toString())

                    if (body == "yes") {
                        Toast.makeText(
                            applicationContext,
                            "앱 인증이 완료되었습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "방 입장 실패",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            })
    }

}