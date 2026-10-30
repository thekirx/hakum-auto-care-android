package com.hakum.customer.ui.queue

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.hakum.customer.data.model.*
import com.hakum.customer.demo.DemoCatalog
import com.hakum.customer.ui.components.*

@Composable fun QueueScreen(data:Snapshot,navigate:(String)->Unit){
    var branch by rememberSaveable{mutableStateOf("bacoor")}
    Page{Heading("Live queue","Your car, every step of the way.");Choice("Branch",DemoCatalog.branches.toList(),branch){branch=it}
        val q=data.queue(branch)
        Panel{Eyebrow("Local simulation");Text("${q.waiting} queued  /  ${q.inProgress} in progress  /  ${q.checking} checking");Muted("Queue changes when Demo Controls advances a booking. No estimated wait is invented.")}
        Section("Your cars on this floor")
        val active=data.active.filter{it.branch==branch && it.status!="pending"}
        if(active.isEmpty())Empty("No car on this floor","Choose another branch or make a booking.")
        active.forEach{VisitCard(it,data){navigate("visit/${it.id}")}}
        val scheduled=data.active.filter{it.branch==branch && it.status=="pending"}
        if(scheduled.isNotEmpty()){Section("Scheduled · not on the floor");scheduled.forEach{b->MenuRow("#${b.number} · Booked","${b.date} · ${b.time}"){navigate("visit/${b.id}")}}}
        MenuRow("Book a service","Choose your next visit"){navigate("book")}
    }
}
@Composable fun VisitScreen(id:String,data:Snapshot){Page{
    Heading("Your visit","Follow the progress of your car.")
    val b=data.bookings.find{it.id==id}
    if(b==null)Empty("Visit unavailable","This demo may have been reset.") else {
        VisitCard(b,data){}
        Panel{VisitSteps.forEach{step->Text("${if(VisitSteps.indexOf(step)<=VisitSteps.indexOf(b.status))"✓" else "○"}  ${statusLabel(step)}")};Muted(if(b.paid)"Paid locally · loyalty credited once" else "Payment and progress are simulated from Settings → Demo Controls.")}
    }
}}
