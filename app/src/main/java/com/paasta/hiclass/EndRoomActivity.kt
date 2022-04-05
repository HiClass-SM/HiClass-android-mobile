package com.paasta.hiclass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.paasta.hiclass.databinding.ActivityEndRoomBinding
import com.paasta.hiclass.databinding.ActivityLoginBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class EndRoomActivity : AppCompatActivity() {
    private var dbemail: String? = null
    private  var mBinding: ActivityEndRoomBinding?=null
    private  val binding get()=mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_room)

        init()

    }
    private fun init(){
        mBinding = ActivityEndRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nonperson:Int
        var realcount = Accessibility.count
        Log.d("횟수", realcount.toString())
        val person = intent.getStringExtra("nonperson")
        nonperson= person!!.toInt()
        Log.d("자리이탈횟수", nonperson.toString())
        val roomname = intent.getStringExtra("roomname")
        dbemail = LoginActivity.prefs.getString("email","")
        if (roomname != null) {
            sendCount(dbemail.toString(),realcount,nonperson,roomname)
        }
        Accessibility.count =0

        addEventListener()
    }

    private fun addEventListener() {

        binding.btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    //back 키 X
    override fun onBackPressed() {
//        startActivity(Intent(this, Home::class.java))
    }


    fun sendCount(email:String,count: Int,nonperson:Int,roomname:String) {
        RetrofitClient.retrofitservice.requestsendcount(email,count,nonperson,roomname)
            .enqueue(object :
                retrofit2.Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        "전송 실패"+t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    Toast.makeText(
                        applicationContext,
                        "방 퇴장"+response.body(),
                        Toast.LENGTH_LONG
                    ).show()
                    val body = response.body()
                    Log.d("방 퇴장", body.toString())
                    if(body!=null) {
                        Toast.makeText(
                            applicationContext,
                            "방을 나갔습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            })
    }
}