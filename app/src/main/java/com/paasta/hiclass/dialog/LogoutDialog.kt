package com.paasta.hiclass.dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.paasta.hiclass.databinding.DialogLogoutBinding


class LogoutDialog: DialogFragment() {
    private var _binding: DialogLogoutBinding? = null
    private val binding get() = _binding!!

    private var fragment: Fragment? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogLogoutBinding.inflate(inflater, container, false)
        val view = binding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        fragment = getActivity()?.getSupportFragmentManager()?.findFragmentByTag("logout");
        binding.btnLogout.setOnClickListener {
            if (fragment != null) {
                val dialogFragment = fragment as DialogFragment
                dialogFragment.dismiss()
            }
        }
        binding.btnLogoutCancel.setOnClickListener {
            dismiss()
        }
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
            params.horizontalMargin = 0.0f
            dialog!!.window!!.attributes = params
        } catch (e: Exception) {
            // regardless
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}