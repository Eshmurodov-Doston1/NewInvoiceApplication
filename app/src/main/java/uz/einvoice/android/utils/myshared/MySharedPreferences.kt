package uz.einvoice.android.utils.myshared

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import uz.einvoice.android.utils.appConstant.AppConstant.ACCESS_TOKEN
import uz.einvoice.android.utils.appConstant.AppConstant.COMPANY_NAME
import uz.einvoice.android.utils.appConstant.AppConstant.EMPTY
import uz.einvoice.android.utils.appConstant.AppConstant.LANG
import uz.einvoice.android.utils.appConstant.AppConstant.REFRESH_TOKEN
import uz.einvoice.android.utils.appConstant.AppConstant.RU
import uz.einvoice.android.utils.appConstant.AppConstant.THEME
import uz.einvoice.android.utils.appConstant.AppConstant.TOKEN_TYPE
import uz.einvoice.android.utils.appConstant.AppConstant.USER_DATA
import javax.inject.Inject

class MySharedPreferences @Inject constructor(
    @ApplicationContext var context: Context
) {
    private val NAME = COMPANY_NAME
    private val MODE = Context.MODE_PRIVATE
    private val sharedPreferences:SharedPreferences = context.getSharedPreferences(NAME,MODE)

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }


    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    fun clearAuth(){
        sharedPreferences.edit().remove(ACCESS_TOKEN).apply()
        sharedPreferences.edit().remove(REFRESH_TOKEN).apply()
        sharedPreferences.edit().remove(TOKEN_TYPE).apply()
    }

    // TODO: access_token
    var accessToken:String?
        get() = sharedPreferences.getString(ACCESS_TOKEN, EMPTY)
        set(value) = sharedPreferences.edit{
            if (value!=null) it.putString(ACCESS_TOKEN,value)
        }
    // TODO: refresh_token
    var refreshToken:String?
        get() = sharedPreferences.getString(REFRESH_TOKEN, EMPTY)
        set(value) = sharedPreferences.edit{
            if (value!=null) it.putString(REFRESH_TOKEN,value)
        }
    // TODO: Token_type
    var tokenType:String?
        get() = sharedPreferences.getString(TOKEN_TYPE, EMPTY)
        set(value) = sharedPreferences.edit{
            if (value!=null && value!="") it.putString(TOKEN_TYPE,value)
        }

    // TODO: user data
    var userData:String?
        get() = sharedPreferences.getString(USER_DATA, EMPTY)
        set(value) = sharedPreferences.edit{
            if (value!=null && value!="") it.putString(USER_DATA,value)
        }

    // TODO: language
    var lang:String?
        get() = sharedPreferences.getString(LANG, RU)
        set(value) = sharedPreferences.edit{
            if (value!=null) it.putString(LANG,value)
        }
    // TODO: theme
    var theme:Boolean?
        get() = sharedPreferences.getBoolean(THEME,false)
        set(value) = sharedPreferences.edit{
            if (value!=null) it.putBoolean(THEME,value)
        }
}