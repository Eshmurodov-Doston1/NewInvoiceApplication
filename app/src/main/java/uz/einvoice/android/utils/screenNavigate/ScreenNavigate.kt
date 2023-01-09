package uz.einvoice.android.utils.screenNavigate

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import uz.einvoice.android.R
import uz.einvoice.android.utils.appConstant.AppConstant.DOCUMENT_ID
import uz.einvoice.android.utils.appConstant.AppConstant.DOCUMENT_ID_APP
import uz.einvoice.android.utils.appConstant.AppConstant.DOCUMENT_STATE_ID
import uz.einvoice.android.utils.appConstant.AppConstant.DOC_STATUS
import uz.einvoice.android.utils.appConstant.AppConstant.DOC_TYPE
import uz.einvoice.android.utils.appConstant.AppConstant.STATE_ID

class ScreenNavigate(
    private val navController: NavController
) {
    fun createDocument(actId:String,docStatus:Int,stateId:Int,documentStateId:Int?,docType:String?){
        val bundle = Bundle()
        bundle.putString(DOCUMENT_ID,actId)
        bundle.putString(DOC_TYPE,docType)
        bundle.putInt(DOC_STATUS,docStatus)
        bundle.putInt(STATE_ID,stateId)
        bundle.putInt(DOCUMENT_STATE_ID,documentStateId?:-1)
        navController.navigate(R.id.documentFragment,bundle,animationViewCreateRight())
    }

    fun createUpdateDocumentScreen(documentId:Int,docId:String?){
        val bundle = Bundle()
        bundle.putInt(DOCUMENT_ID_APP,documentId)
        bundle.putString(DOCUMENT_ID,docId)
        navController.navigate(R.id.action_documentFragment_to_updateFragment,bundle,animationViewCreateRight())
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