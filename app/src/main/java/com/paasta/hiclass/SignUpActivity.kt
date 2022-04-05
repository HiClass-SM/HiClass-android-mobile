package com.paasta.hiclass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.paasta.hiclass.databinding.ActivitySignUpBinding
import com.paasta.hiclass.model.DataViewModel
import com.paasta.hiclass.model.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private val viewModel: DataViewModel by viewModels()
    private  var mBinding:ActivitySignUpBinding?=null
    private  val binding get()=mBinding!!
    private var role:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        init()
    }
    private fun init(){
        mBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addEventListener()


    }
    private fun addEventListener() {


        binding.radiobtnSignup.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.radiobtn_teacher -> this.role="teacher"
                R.id.radiobtn_student -> this.role="student"
            }
        }

        binding.btnSignup.setOnClickListener {

            if (binding.editEmail.text.toString() != null && binding.editPassword.text.toString() != null && binding.editName.text.toString() != null) {
                //서버로 데이터를 보내줌
                signUp(
                    binding.editEmail.text.toString(),
                    binding.editPassword.text.toString(),
                    binding.editName.text.toString(),
                    role
                )
            } else {
                Toast.makeText(this, "빈칸없이 입력해주세요", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun signUp(email: String, password: String, name: String, role: String){
        RetrofitClient.retrofitservice.requestSignUp(
            email,
            password,
            name,
            role
        ).enqueue(object : Callback<UserData> {
            override fun onFailure(call: Call<UserData>, t: Throwable) {
                //실패시 작업 -> 간단하게 Dialog 띄어주기
                Toast.makeText(applicationContext, "회원가입 실패", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                //데이터를 받아서 저장해줌.

                val body = response.body()
                Log.d("통신응답", response.body()?.name.toString())
                if(response.body()?.name!="Fail") {
                    LoginActivity.prefs.setString("email",binding.editEmail.text.toString())
                    LoginActivity.prefs.setString("password",binding.editPassword.text.toString())
                    LoginActivity.prefs.setString("role",role)
                    Toast.makeText(applicationContext, "홈 화면으로 이동합니다", Toast.LENGTH_LONG).show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
//                    progressDialog.cancel()
                }else {
                    Toast.makeText(applicationContext, "해당 이메일은 이미 가입되어 있습니다", Toast.LENGTH_SHORT).show()
//                    progressDialog.cancel()
                }

            }
        })


    }
}