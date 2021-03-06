package com.sethchhim.kuboo_remote.task

import androidx.lifecycle.MutableLiveData
import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import timber.log.Timber

class Task_RemoteUserApiPut(kubooRemote: KubooRemote, login: Login, book: Book) : Task_RemoteUserApiBase(kubooRemote, login, book) {

    internal val liveData = MutableLiveData<Boolean>()

    init {
        kubooRemote.networkIO.execute {
            try {
                val call = okHttpHelper.putCall(login, stringUrl, getRequestBody())
                val response = call.execute()
                if (response.isSuccessful) {
                    Timber.d("UserApi put is successful. title[${book.title}] page[${book.currentPage} of ${book.totalPages}] bookMark[${book.bookMark}] isFinished[${book.isFinished}] stringUrl[$stringUrl]")
                    kubooRemote.mainThread.execute { liveData.value = true }
                } else {
                    when (response.code()) {
                        401 -> handleAuthentication(call)
                        else -> {
                            Timber.e("code[${response.code()}] message[${response.message()}] title[${book.title}] stringUrl[$stringUrl]")
                            kubooRemote.mainThread.execute { liveData.value = false }
                        }
                    }
                }
                response.close()
            } catch (e: Exception) {
                Timber.e("message[${e.message}] stringUrl[$stringUrl]")
                kubooRemote.mainThread.execute { liveData.value = false }
            }
        }
    }

    private fun handleAuthentication(call: Call) = kubooRemote.mainThread.execute {
        Task_Authenticate(kubooRemote, login).liveData.observeForever { result ->
            if (result == true) {
                val secondCall = call.clone()
                secondCall.retry()
            }
        }
    }

    private fun Call.retry() = kubooRemote.networkIO.execute {
        try {
            val secondResponse = execute()
            if (secondResponse.isSuccessful) {
                Timber.i("UserApi put is successful. title[${book.title}] page[${book.currentPage} of ${book.totalPages}] bookMark[${book.bookMark}] isFinished[${book.isFinished}] stringUrl[$stringUrl]")
            } else {
                Timber.e("code[${secondResponse.code()}] message[${secondResponse.message()}] title[${book.title}] stringUrl[$stringUrl] secondAttempt[true]")
                kubooRemote.mainThread.execute { liveData.value = null }
            }
        } catch (e: Exception) {
            Timber.e("message[${e.message}] secondAttempt[true]")
        }
    }

    private fun getRequestBody(): RequestBody {
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val params = HashMap<String, String>().apply {
            val mark = when (book.isEpub()) {
                true -> book.bookMark
                false -> try {
                    book.currentPage.toString()
                } catch (e: Exception) {
                    "0"
                }
            }
            put("mark", mark)
            put("isFinished", book.isFinished.toString())
        }
        val jsonObject = JSONObject(params)
        val parameter = jsonObject.toString()
        return RequestBody.create(JSON, parameter)
    }

}