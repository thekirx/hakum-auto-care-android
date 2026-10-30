package com.hakum.customer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.hakum.customer.data.local.*
import com.hakum.customer.data.model.Snapshot
import com.hakum.customer.data.repository.CustomerRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CustomerUiState(val data:Snapshot?=null,val preferences:Preferences=Preferences())
class CustomerViewModel(application:Application):AndroidViewModel(application) {
    private val db=Room.databaseBuilder(application,HakumDatabase::class.java,"hakum-customer.db").build()
    val repository=CustomerRepository(db)
    val preferences=PreferenceStore(application)
    private val notices=Channel<String>(Channel.BUFFERED)
    val messages=notices.receiveAsFlow()
    val state=combine(repository.changes,preferences.state){data,prefs->CustomerUiState(data,prefs)}.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),CustomerUiState())
    init {action{repository.seed()}}
    fun action(success:String="",block:suspend ()->Unit) {viewModelScope.launch {try {block();if(success.isNotEmpty())notices.send(success)}catch(e:Exception){notices.send(e.message?:"Unable to finish. Try again.")}}}
    fun login(email:String,password:String){action{require(repository.signIn(email,password)){"Email or password does not match the local account"};preferences.session(true)}}
    fun demoLogin(){action{repository.demoLogin();preferences.session(true)}}
    override fun onCleared(){db.close()}
}
