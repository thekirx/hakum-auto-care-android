package com.hakum.customer.demo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.hakum.customer.CustomerViewModel
import com.hakum.customer.data.model.Snapshot
import com.hakum.customer.ui.components.*
import com.hakum.customer.ui.profile.Toggle

@Composable fun DemoControlsScreen(data:Snapshot,vm:CustomerViewModel,navigate:(String)->Unit){
    var reset by remember{mutableStateOf(false)};var slots by rememberSaveable(data.settings.slots){mutableStateOf(data.settings.slots.toString())};var service by rememberSaveable{mutableStateOf("wash")}
    Page{Heading("Demo Controls","Testing tools · fictional customer data only.")
        Panel{Eyebrow("Complete a paid service");Choice("Service",data.services.map{it.id to it.name},service){service=it};Muted("Creates and completes a local paid visit. Stamps follow the selected policy; points use the service catalog and eligible membership multiplier.")
            Primary("Simulate completed paid service"){vm.action("Service completed · stamps and points updated"){vm.repository.simulateService(service)}}
        }
        Section("Advance your bookings")
        data.active.forEach{b->Panel{Text("#${b.number} · ${data.services.find{it.id==b.serviceId}?.name}");Muted(statusLabel(b.status));Primary("Advance to next step"){vm.action("Queue updated"){vm.repository.advance(b.id)}};OutlinedButton({vm.action("Completed and marked paid"){vm.repository.complete(b.id)}}){Text("Complete & mark paid")}}}
        data.bookings.firstOrNull{it.paid}?.let{b->OutlinedButton({vm.action("Duplicate completion ignored · no additional credit"){vm.repository.complete(b.id)}}){Text("Retry last completion (idempotency)")}}
        Panel{Eyebrow("Program configuration");OutlinedTextField(slots,{slots=it},label={Text("Stamp card slots · 5–50")},singleLine=true,modifier=Modifier.fillMaxWidth());Primary("Save card capacity"){vm.action("Card capacity updated"){vm.repository.configure(data.settings.copy(slots=slots.toIntOrNull()?:0))}}
            Choice("Stamp award policy",listOf("visit" to "Per visit (current)","all_weighted" to "Weighted helper","pay_categories" to "Wash category helper"),data.settings.earnMode){mode->vm.action{vm.repository.configure(data.settings.copy(earnMode=mode))}}
            Choice("Allowed weighted categories",listOf("wash" to "Wash","detailing" to "Detailing","wash,detailing,ppf" to "All demo categories"),data.settings.payCategories){v->vm.action{vm.repository.configure(data.settings.copy(payCategories=v))}}
            val selectedService=data.services.first{it.id==service}
            Choice("Selected service stamp weight",listOf(0,1,2,3,5).map{it.toString() to it.toString()},selectedService.weight.toString()){v->vm.action{vm.repository.configureService(service,v.toInt())}}
            Toggle("Stamps enabled",data.settings.stampsEnabled){v->vm.action{vm.repository.configure(data.settings.copy(stampsEnabled=v))}}
            Toggle("Points enabled",data.settings.pointsEnabled){v->vm.action{vm.repository.configure(data.settings.copy(pointsEnabled=v))}}
            Toggle("Memberships enabled",data.settings.membershipsEnabled){v->vm.action{vm.repository.configure(data.settings.copy(membershipsEnabled=v))}}
            Toggle("Demo membership ×1.5",data.membership.active){v->vm.action{vm.repository.setMembership(v)}}
            Toggle("Multiply weighted stamps",data.settings.multiplyStamps){v->vm.action{vm.repository.configure(data.settings.copy(multiplyStamps=v))}}
            Toggle("Wrap at card capacity",data.settings.wrap){v->vm.action{vm.repository.configure(data.settings.copy(wrap=v))}}
            Muted("Current source policy: 1 stamp per paid visit, service points_award. Helper modes reproduce loyaltyLogic.js. Wrap can remove an unclaimed milestone's stamp balance, as in the source.")
        }
        MenuRow("Inspect earned rewards","See your card and claim status"){navigate("rewards")}
        Primary("Populate sample visit history"){vm.action("Five sample visits added once"){vm.repository.sampleHistory()}}
        OutlinedButton({vm.action("Demo birthday is today"){vm.repository.demoBirthdayToday()}}){Text("Simulate birthday today")}
        OutlinedButton({vm.action("Visit history cleared"){vm.repository.clearHistory()}}){Text("Clear history (empty-state test)")}
        Section("Stamp ledger")
        data.stamps.takeLast(12).reversed().forEach{Muted("${it.delta} stamps · ${it.visitId.take(16)}")}
        Section("Points ledger")
        data.points.take(12).forEach{Muted("${it.delta} points · ${it.reason}")}
        OutlinedButton({reset=true}){Text("Reset demo customer")}
    }
    if(reset)AlertDialog(onDismissRequest={reset=false},title={Text("Reset local demo?")},text={Text("This replaces local profile, vehicles, bookings and redemptions with Alex's fictional sample data. Theme and notification preferences are retained.")},confirmButton={TextButton({reset=false;vm.action("Demo customer reset"){vm.repository.reset()}}){Text("Reset demo")}},dismissButton={TextButton({reset=false}){Text("Cancel")}})
}
