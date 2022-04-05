package com.paasta.hiclass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.paasta.hiclass.databinding.ActivityLoginBinding
import com.paasta.hiclass.databinding.ActivitySettingCameraBinding
import kotlinx.android.synthetic.main.activity_test.*
import org.json.JSONObject

class SettingCameraActivity : AppCompatActivity() {
    private var dbname: String? = null
    private var dbemail: String? = null
    private var roomname:String? = null

    private  var mBinding: ActivitySettingCameraBinding?=null
    private  val binding get()=mBinding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_camera)
        init()

    }
    private fun init(){
        mBinding = ActivitySettingCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        roomname = intent.getStringExtra("roomname")

        addEventListener()
    }

    private fun addEventListener() {

        binding.btnDone.setOnClickListener {
            val intent = Intent(applicationContext, OpencvActivity::class.java)
            intent.putExtra("roomname", roomname)
            intent.putExtra("email", dbemail)
            startActivity(intent)
        }
    }

    //back 키 누르면 홈으로
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}