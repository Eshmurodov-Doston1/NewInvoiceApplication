package uz.einvoice.android.utils.uiStates

import androidx.lifecycle.LiveData
import uz.einvoice.domain.models.menuModel.Data

interface UIController {
    fun loadingSave(isLoading:Boolean)
    fun clickMenu():LiveData<Data>
    fun bottomBarView(isVisible:Boolean)
}