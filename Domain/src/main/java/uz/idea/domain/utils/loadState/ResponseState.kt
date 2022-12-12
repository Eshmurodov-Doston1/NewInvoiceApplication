package uz.idea.domain.utils.loadState


sealed class ResponseState<out T> {
    object Loading: ResponseState<Nothing>()
    data class Success<T>(val data:T): ResponseState<T>()
    data class Error(var exception: Throwable): ResponseState<Nothing>()
}