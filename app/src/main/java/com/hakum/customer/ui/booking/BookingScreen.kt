package com.hakum.customer.ui.booking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hakum.customer.CustomerViewModel
import com.hakum.customer.data.model.*
import com.hakum.customer.demo.DemoCatalog
import com.hakum.customer.ui.components.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable fun BookingScreen(data:Snapshot,vm:CustomerViewModel,navigate:(String)->Unit){
    var branch by rememberSaveable{mutableStateOf("bacoor")};var vehicle by rememberSaveable{mutableStateOf(data.vehicles.firstOrNull()?.id?:"")};var service by rememberSaveable{mutableStateOf("express")}
    var date by rememberSaveable{mutableStateOf("")};var time by rememberSaveable{mutableStateOf("")};var review by rememberSaveable{mutableStateOf(false)}
    Page{Heading("Book a service","Choose your care. We'll take it from here.");Eyebrow("01 / Your visit")
        Choice("Branch",DemoCatalog.branches.toList(),branch){branch=it}
        if(data.vehicles.isEmpty())MenuRow("Add your first vehicle","Save your plate before booking"){navigate("vehicles")} else Choice("Your car",data.vehicles.map{it.id to it.plate},vehicle){vehicle=it}
        Section("Choose your service")
        data.services.forEach{s->OutlinedCard(onClick={service=s.id},border=androidx.compose.foundation.BorderStroke(if(service==s.id)2.dp else 1.dp,if(service==s.id)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),modifier=Modifier.fillMaxWidth()){
            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){Row{Text(s.name,style=MaterialTheme.typography.titleMedium,modifier=Modifier.weight(1f));RadioButton(service==s.id,{service=s.id})};Muted(s.description);Text("${money(s.priceMinor)} · ${s.points} points")}
        }}
        Eyebrow("02 / A time for your car")
        Muted("Tentative demo booking · dates start two days ahead, matching the source booking flow.")
        Choice("Date",(2L..8L).map{val d=vm.repository.today().plusDays(it);d.toString() to d.format(DateTimeFormatter.ofPattern("EEE, MMM d"))},date){date=it}
        Choice("Time",listOf("09:00","10:00","11:00","13:00","14:00","15:00","16:00").map{it to it},time){time=it}
        Primary("Review booking",vehicle.isNotEmpty()&&date.isNotEmpty()&&time.isNotEmpty()){review=true}
        Section("Your bookings")
        if(data.bookings.isEmpty())Empty("No bookings yet","Your confirmed local request will appear here.")
        data.bookings.forEach{b->MenuRow("#${b.number} · ${data.services.find{it.id==b.serviceId}?.name}","${b.date} ${b.time} · ${statusLabel(b.status)}"){navigate("visit/${b.id}")}}
    }
    if(review)AlertDialog(onDismissRequest={review=false},title={Text("Review your booking")},text={Text("${DemoCatalog.branches[branch]}\n${data.vehicles.find{it.id==vehicle}?.plate}\n${data.services.find{it.id==service}?.name}\n$date · $time\n${money(data.services.first{it.id==service}.priceMinor)}\n\nDemo only. No payment is collected.")},confirmButton={TextButton({review=false;vm.action("Booking saved"){val id=vm.repository.book(branch,vehicle,service,date,time);navigate("visit/$id")}}){Text("Confirm booking")}},dismissButton={TextButton({review=false}){Text("Edit")}})
}
