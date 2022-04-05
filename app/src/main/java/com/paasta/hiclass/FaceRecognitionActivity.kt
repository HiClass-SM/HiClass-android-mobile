package com.paasta.hiclass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.paasta.hiclass.databinding.ActivityFaceRecognitionBinding
import com.paasta.hiclass.databinding.ActivityLoginBinding

class FaceRecognitionActivity : AppCompatActivity() {

    private  var mBinding: ActivityFaceRecognitionBinding?=null
    private  val binding get()=mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_recognition)

        init()

    }
    private fun init(){
        mBinding = ActivityFaceRecognitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val result = intent.getStringExtra("result")
        val roomname = intent.getStringExtra("roomname")
        Log.d("얼굴인식", result.toString())

        if(result=="fail"){
            binding.imgResult.setImageResource(R.drawable.fail)
            binding.txtResult1.setText("얼굴 인식 실패 !")
            binding.txtResult2.setText("다시 시도해 주세요")
            binding.txtResult3.setVisibility(View.INVISIBLE);
            binding.btnNext.setText("Again")
            binding.btnNext.setOnClickListener {
                finish()
            }
        }else if(result=="success"){
            binding.btnNext.setOnClickListener {
                    val intent = Intent(applicationContext, BlockAppActivity::class.java)
                    intent.putExtra("roomname", roomname)
                    startActivity(intent)
            }
        }
    }

    //back 키 누르면 홈으로
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}