package com.hakum.customer.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.hakum.customer.CustomerViewModel
import com.hakum.customer.data.model.*
import com.hakum.customer.domain.loyalty.LoyaltyEngine
import com.hakum.customer.ui.components.*

@Composable fun MeScreen(data:Snapshot,navigate:(String)->Unit){Page{
    Heading("Me","Your car care, your way.")
    Panel{Text(data.customer.name,style=MaterialTheme.typography.headlineSmall);Muted(data.customer.email);Text(LoyaltyEngine.tier(data.visits.size))}
    MenuRow("Profile & settings","Account, appearance and notifications",Icons.Outlined.Settings){navigate("settings")}
    MenuRow("My vehicles","${data.vehicles.size} saved vehicles",Icons.Outlined.DirectionsCar){navigate("vehicles")}
    MenuRow("Visit history","${data.visits.size} completed visits",Icons.Outlined.History){navigate("history")}
    MenuRow("Loyalty overview","${data.account.stamps} stamps · ${data.account.points} points",Icons.Outlined.CardGiftcard){navigate("rewards")}
    MenuRow("Vouchers","Your birthday wash and points shop",Icons.Outlined.ConfirmationNumber){navigate("vouchers")}
}}
@Composable fun VehiclesScreen(data:Snapshot,vm:CustomerViewModel){
    var plate by rememberSaveable{mutableStateOf("")};var make by rememberSaveable{mutableStateOf("")};var model by rememberSaveable{mutableStateOf("")}
    Page{Heading("My vehicles","Your garage, ready for your next visit.")
        if(data.vehicles.isEmpty())Empty("Your garage is empty","Add a car below to start booking.")
        data.vehicles.forEach{v->Panel{Text(v.plate,style=MaterialTheme.typography.headlineSmall);Muted("${v.make} ${v.model} · ${v.color} · ${v.size}")}}
        Panel{Section("Add a vehicle");OutlinedTextField(plate,{plate=it},label={Text("Plate number")},singleLine=true,modifier=Modifier.fillMaxWidth());OutlinedTextField(make,{make=it},label={Text("Make")},singleLine=true,modifier=Modifier.fillMaxWidth());OutlinedTextField(model,{model=it},label={Text("Model")},singleLine=true,modifier=Modifier.fillMaxWidth());Primary("Save vehicle"){vm.action("Vehicle saved"){vm.repository.addVehicle(plate,make,model);plate="";make="";model=""}}}
    }
}
@Composable fun HistoryScreen(data:Snapshot){Page{Heading("Visit history","Every visit. Every bit of care.")
    if(data.visits.isEmpty())Empty("No completed visits","Complete a paid service in Demo Controls to see your history.")
    data.visits.forEach{v->Panel{Text(v.service,style=MaterialTheme.typography.titleMedium);Muted("${v.date} · ${v.plate} · ${v.branch}");Text("${money(v.amountMinor)} · Paid")}}
}}
@Composable fun SettingsScreen(data:Snapshot,dark:Boolean,notifications:Boolean,vm:CustomerViewModel,navigate:(String)->Unit){
    var name by rememberSaveable(data.customer.name){mutableStateOf(data.customer.name)};var phone by rememberSaveable(data.customer.phone){mutableStateOf(data.customer.phone)};var birthday by rememberSaveable(data.customer.birthday){mutableStateOf(data.customer.birthday)}
    Page{Heading("Settings","Make yourself at home.")
        Panel{Text("Your profile",style=MaterialTheme.typography.titleLarge);OutlinedTextField(name,{name=it},label={Text("Full name")},singleLine=true,modifier=Modifier.fillMaxWidth());OutlinedTextField(phone,{phone=it},label={Text("Phone")},singleLine=true,modifier=Modifier.fillMaxWidth());OutlinedTextField(birthday,{birthday=it},label={Text("Birthday · YYYY-MM-DD")},readOnly=data.customer.birthday.isNotBlank(),singleLine=true,modifier=Modifier.fillMaxWidth());Muted("Birthday is locked after the first save.");Primary("Save profile"){vm.action("Profile saved"){vm.repository.updateProfile(name,phone,birthday)}}}
        Panel{Toggle("Dark theme",dark){vm.action{vm.preferences.dark(it)}};Toggle("Service notifications",notifications){vm.action{vm.preferences.notifications(it)}};Muted("Notification preference is saved locally. No messages are sent in this offline demo.")}
        MenuRow("Demo Controls","Simulate service, inspect ledgers and reset"){navigate("demo")}
        OutlinedButton({vm.action{vm.preferences.session(false)}}){Text("Sign out")}
    }
}
@Composable fun Toggle(label:String,checked:Boolean,onChange:(Boolean)->Unit){Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically){Text(label,modifier=Modifier.weight(1f));Switch(checked,onChange)}}
