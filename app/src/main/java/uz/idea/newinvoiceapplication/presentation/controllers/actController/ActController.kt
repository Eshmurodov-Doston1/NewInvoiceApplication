package uz.idea.newinvoiceapplication.presentation.controllers.actController

interface ActController {
    fun createAct()
    fun incomingAct()
    fun outgoingAct()
    fun draftAct()
    fun processSendingAct()
}