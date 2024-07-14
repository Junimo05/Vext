package com.example.vext

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    //Permission Dialog
    val visiblePermissionDialogQueue = mutableListOf<String>()

    fun dismissDialog(){
        visiblePermissionDialogQueue.removeLast()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ){
        if (isGranted){
            visiblePermissionDialogQueue.add(0, permission)
        }
    }


}