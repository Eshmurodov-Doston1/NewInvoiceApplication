package uz.idea.newinvoiceapplication.utils.uiStates

import androidx.lifecycle.LiveData
import uz.idea.domain.models.menuModel.Data

interface UIController {
    fun clickMenu():LiveData<Data>
}