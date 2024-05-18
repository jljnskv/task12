package com.example.task12

import android.provider.Settings.Global.getString

sealed class States {
    object Initial : States()
    object Loading : States()
    object Canceled : States()
    data class Success(val curRequest: String) : States()

    fun isProgress(): Boolean {
        return equals(Loading)
    }

}
