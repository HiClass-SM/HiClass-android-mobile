package com.paasta.hiclass.model

import android.content.ContentUris
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class DataViewModel : ViewModel(){
    val data = ArrayList<UserData>()
    var dataImage = ArrayList<DataImage>()
    var listimage=ArrayList<File>()
    val LiveData = MutableLiveData<ArrayList<UserData>>()
    val LiveDataImage = MutableLiveData<ArrayList<DataImage>>()


    fun setDataImage(item : List<com.esafirm.imagepicker.model.Image> ) {
        //중복되어서 붙여지므로 지워주고 시작함.
        dataImage.clear()
        listimage.clear()
        //이미지 객체를 가져와서 uri 형태로 변환&대입.
        for (i in 0 until item.size) {
            val items =
                ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    item[i].id
                )
            val imagedata = DataImage(Image = items)
            dataImage.add(imagedata)
            //이미지 전송용
            val a = File(item[i].path)
            listimage.add(a)

        }
        LiveDataImage.value=dataImage
    }

    fun deleteDataImage(position: Int) {
//        dataImage.removeAt(position)
//        LiveDataImage.value = dataImage

    }
    fun setData(item : UserData){
        data.add(item)
        LiveData.value=data
    }


}