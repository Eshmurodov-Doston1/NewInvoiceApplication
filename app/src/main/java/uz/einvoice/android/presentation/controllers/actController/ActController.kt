package uz.einvoice.android.presentation.controllers.actController

interface ActController {
    fun createAct()
    fun incomingAct()
    fun outgoingAct()
    fun draftAct()
    fun processSendingAct()
}