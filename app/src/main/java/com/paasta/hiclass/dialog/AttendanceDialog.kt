package com.paasta.hiclass.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.paasta.hiclass.RetrofitClient
import com.paasta.hiclass.RoomCamera
import com.paasta.hiclass.databinding.AttendanceDialogBinding
import retrofit2.Call
import retrofit2.Response


class AttendanceDialog: DialogFragment() {
    private var _binding: AttendanceDialogBinding? = null
    private val binding get() = _binding!!

    private var fragment: Fragment? = null
    private var className : String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = AttendanceDialogBinding.inflate(inflater, container, false)
        val view = binding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        fragment = getActivity()?.getSupportFragmentManager()?.findFragmentByTag("tag");
        binding.btnEnter.setOnClickListener {
            if (fragment != null) {
                val dialogFragment = fragment as DialogFragment
                dialogFragment.dismiss()
            }
        }
        val mArgs = getArguments()
        className = mArgs!!.getString("classname").toString()
        Log.d("반이름", className.toString())
        addEventListener()
        return view
    }
    override fun onResume() {
        super.onResume()

//      dialog fragment custom width
        try {
            val windowManager =
                requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val deviceSize = Point()
            display.getSize(deviceSize)
            val params = dialog!!.window!!.attributes
            params.width = deviceSize.x-200
            params.height = 890
            params.horizontalMargin = 0.0f
            dialog!!.window!!.attributes = params
        } catch (e: Exception) {
            // regardless
            e.printStackTrace()
        }
    }
    private fun addEventListener() {
        binding.btnEnter.setOnClickListener{
           checkinfo(binding.editName?.text.toString(),binding.editNumber?.text.toString())
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun checkinfo(name: String, number: String) {
        RetrofitClient.retrofitservice.requestAttendance(className,number, name)
            .enqueue(object : retrofit2.Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(context, "전송 실패"+t.message, Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Toast.makeText(context, "입장"+response.body(), Toast.LENGTH_LONG).show()
                    val body = response.body()
                    Log.d("신원확인", body.toString())

                    if (body != null) {
                        //학번 이름 일치

                        if(body=="nomatch"){
                            //학번 이름 불일치
                            Toast.makeText(context, "학번과 이름이 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                        }else if(body=="nonum") {
                            //명단에 없음
                            Toast.makeText(context, "명단에 존재하지 않습니다.", Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(context, "얼굴 인식 페이지로 이동합니다.", Toast.LENGTH_LONG).show()
                            val sucessintent = Intent(getActivity()?.getApplicationContext(), RoomCamera::class.java)
                            sucessintent.putExtra("index", body)
                            sucessintent.putExtra("classname", className)
                            startActivity(sucessintent)
                        }
                    }else {
                        Toast.makeText(context, "실패"+response.body(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }
}