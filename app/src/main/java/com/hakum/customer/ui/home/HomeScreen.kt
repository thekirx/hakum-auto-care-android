package com.hakum.customer.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import com.hakum.customer.data.model.*
import com.hakum.customer.domain.loyalty.LoyaltyEngine
import com.hakum.customer.ui.components.*
import java.time.*

@Composable fun HomeScreen(data:Snapshot,navigate:(String)->Unit){Page{
    val hour=ZonedDateTime.now(ZoneId.of("Asia/Manila")).hour
    Eyebrow(if(hour<12)"Good morning" else if(hour<18)"Good afternoon" else "Good evening")
    Heading("Hi, ${data.customer.name.substringBefore(' ')}.","A little care goes a long way.")
    data.active.firstOrNull()?.let{VisitCard(it,data){navigate("visit/${it.id}")}} ?: Empty("Your next great drive starts here","No active visit. Book a service when you're ready.")
    Primary("Book a service"){navigate("book")}
    Section("Your shortcuts")
    MenuRow("My vehicles","${data.vehicles.size} in your garage",Icons.Outlined.DirectionsCar){navigate("vehicles")}
    MenuRow("Rewards & vouchers","${data.account.points} points · ${LoyaltyEngine.tier(data.visits.size)}",Icons.Outlined.CardGiftcard){navigate("rewards")}
    Section("Your loyalty card","View card"){navigate("rewards")}
    if(data.settings.stampsEnabled)StampCard(data,true) else Empty("Stamps are paused","Completed visits still appear in your history.")
    Section("Queue at a glance","View queue"){navigate("queue")}
    val q=data.queue(data.active.firstOrNull()?.branch?:"bacoor")
    Panel{Text("${q.waiting} queued  ·  ${q.inProgress} in progress  ·  ${q.checking} checking");Muted("Local demo queue · your bookings only")}
    Section("Latest visits","View all"){navigate("history")}
    if(data.visits.isEmpty())Empty("No visits yet","Your completed services will appear here.")
    data.visits.take(3).forEach{v->Panel{Text(v.service,style=MaterialTheme.typography.titleMedium);Muted("${v.plate} · ${v.date}");Text(money(v.amountMinor))}}
}}
