package uz.idea.newinvoiceapplication.utils.screenNavigate

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.DOCUMENT_ID
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.DOC_STATUS

class ScreenNavigate(
    private val navController: NavController
) {
    fun createDocument(actId:String,docStatus:Int){
        val bundle = Bundle()
        bundle.putString(DOCUMENT_ID,actId)
        bundle.putInt(DOC_STATUS,docStatus)
        navController.navigate(R.id.action_home_to_documentFragment,bundle,animationViewCreateRight())
    }

    private fun animationViewCreateRight(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.slide_out)
            .build()
    }
}