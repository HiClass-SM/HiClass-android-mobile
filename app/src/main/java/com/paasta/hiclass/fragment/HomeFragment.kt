package com.paasta.hiclass.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.paasta.hiclass.*
import com.paasta.hiclass.dialog.AttendanceDialog
import retrofit2.Call
import retrofit2.Response


class HomeFragment : Fragment() {

    private var name : String =""
    private var email : String =""


    private lateinit var enterClass: Button
    private lateinit var className: EditText
    private lateinit var classPassword: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, null)
        // 처리
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enterClass = view.findViewById(R.id.btn_enter)
        className= view.findViewById<EditText>(R.id.edit_class_name)
        classPassword= view.findViewById<EditText>(R.id.edit_class_password)

//        name = LoginActivity.prefs.getString("name","")
//        email = LoginActivity.prefs.getString("email","")


        addEventListener()

    }

    private fun addEventListener() {
        enterClass.setOnClickListener{

            if(className?.text.toString()!=""){
                if(classPassword?.text.toString()!=""){

                    enterroom(className?.text.toString(), classPassword?.text.toString())
                }else{
                    Toast.makeText(getActivity(), "방 비밀번호를 입력해주세요", Toast.LENGTH_LONG).show()
                }
            }else
                Toast.makeText(getActivity(), "방 이름을 입력해주세요", Toast.LENGTH_LONG).show()

        }
    }

    fun enterroom(roomname: String, password: String) {
        RetrofitClient.retrofitservice.requestEnterRoom(roomname, password)
            .enqueue(object :
                retrofit2.Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(getActivity(), "전송 실패"+t.message, Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<String>, response: Response<String>) {

                    val body = response.body()
                    Log.d("방입장", body.toString())

                    if(body=="success") {
                        Log.d("방입장!!!!!!!!!!",roomname)
                        val args = Bundle()
                        args.putString("classname",roomname)
                        val attendanceDialog = AttendanceDialog()
                        attendanceDialog.setArguments(args)
                        getActivity()?.getSupportFragmentManager()?.let { it1 -> attendanceDialog.show(it1,"tag") }
                    }else if(body=="fail") {
                        Toast.makeText(getActivity(), "방 비밀번호가 틀립니다.", Toast.LENGTH_LONG).show()
                    }else if(body=="NoRoom") {
                        Toast.makeText(getActivity(), "해당 방이 없습니다.", Toast.LENGTH_LONG).show()
                    }else {
                        Toast.makeText(getActivity(), "실패"+response.body(), Toast.LENGTH_LONG).show()
                    }

                }
            })
    }

}