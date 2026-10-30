package com.hakum.customer.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hakum.customer.R
import com.hakum.customer.data.model.*
import com.hakum.customer.demo.DemoCatalog
import com.hakum.customer.domain.loyalty.*
import com.hakum.customer.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

fun money(minor:Long)="₱"+NumberFormat.getNumberInstance(Locale.US).format(minor/100)
@Composable fun Page(content:@Composable ColumnScope.()->Unit){Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=20.dp).padding(top=8.dp,bottom=24.dp),verticalArrangement=Arrangement.spacedBy(18.dp),content=content)}
@Composable fun Heading(title:String,subtitle:String=""){Column(verticalArrangement=Arrangement.spacedBy(8.dp)){Text(title.uppercase(),style=MaterialTheme.typography.headlineLarge);if(subtitle.isNotBlank())Muted(subtitle)}}
@Composable fun Muted(text:String){Text(text,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)}
@Composable fun Eyebrow(text:String){Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){Box(Modifier.size(18.dp,2.dp).background(MaterialTheme.colorScheme.primary));Text(text.uppercase(),style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary)}}
@Composable fun Section(title:String,action:String="",onAction:()->Unit={}){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Text(title,style=MaterialTheme.typography.titleMedium,modifier=Modifier.weight(1f));if(action.isNotEmpty())TextButton(onClick=onAction){Text(action)}}}
@Composable fun Panel(modifier:Modifier=Modifier,navy:Boolean=false,content:@Composable ColumnScope.()->Unit){
    Surface(modifier.fillMaxWidth(),shape=RoundedCornerShape(18.dp),color=if(navy)Navy else MaterialTheme.colorScheme.surface,border=BorderStroke(1.dp,if(navy)Color(0xFF24357A) else MaterialTheme.colorScheme.outline.copy(alpha=.5f))){
        CompositionLocalProvider(LocalContentColor provides if(navy)Paper else MaterialTheme.colorScheme.onSurface){Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp),content=content)}
    }
}
@Composable fun Primary(text:String,enabled:Boolean=true,onClick:()->Unit){Button(onClick,Modifier.fillMaxWidth().heightIn(min=50.dp),enabled=enabled,shape=RoundedCornerShape(28.dp),colors=ButtonDefaults.buttonColors(containerColor=Blue,contentColor=Color.White)){Text(text.uppercase())}}
@Composable fun MenuRow(title:String,sub:String="",icon:ImageVector=Icons.Outlined.ChevronRight,onClick:()->Unit){Surface(onClick=onClick,shape=RoundedCornerShape(14.dp),color=MaterialTheme.colorScheme.surface){Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){Icon(icon,null,tint=MaterialTheme.colorScheme.primary);Column(Modifier.weight(1f)){Text(title,style=MaterialTheme.typography.titleMedium);if(sub.isNotEmpty())Muted(sub)};Icon(Icons.Outlined.ChevronRight,null)}}}
@Composable fun Empty(title:String,sub:String){Panel{Text(title,style=MaterialTheme.typography.titleMedium);Muted(sub)}}
@Composable fun Choice(label:String,options:List<Pair<String,String>>,selected:String,onSelect:(String)->Unit){Column(verticalArrangement=Arrangement.spacedBy(4.dp)){Text(label,style=MaterialTheme.typography.titleMedium);Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)){options.forEach{(id,text)->FilterChip(selected==id,{onSelect(id)},label={Text(text)})}}}}
@Composable fun StampCard(data:Snapshot,compact:Boolean=false){
    val progress=LoyaltyEngine.progress(data.account.stamps,data.settings.slots,data.rewards.filter{it.kind=="stamp"}.map{Milestone(it.threshold,it.label,it.active)})
    Panel(navy=true){
        Row(verticalAlignment=Alignment.CenterVertically){Image(painterResource(R.drawable.hakum_mark_white),"Hakum",Modifier.size(44.dp));Spacer(Modifier.weight(1f));Text("MEMBER",style=MaterialTheme.typography.labelSmall,color=Sky)}
        Text("${progress.completed} of ${progress.slots} stamps",style=MaterialTheme.typography.headlineSmall)
        Text(progress.next?.let{"${it.threshold-progress.completed} more stamps to ${it.label}"}?:"Every reward earned. Show this at the counter.",style=MaterialTheme.typography.bodyMedium,color=Sky)
        if(!compact){
            (1..progress.slots).chunked(5).forEach{row->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                row.forEach{n->val earned=n<=progress.completed;val gift=data.rewards.any{it.kind=="stamp"&&it.threshold==n}
                    Surface(Modifier.weight(1f).aspectRatio(1f).semantics{contentDescription="Stamp $n ${if(earned)"earned" else if(gift)"reward" else "remaining"}"},shape=CircleShape,color=if(earned)Blue else Color(0xFF0D1B52),border=BorderStroke(1.dp,if(earned)Sky else Color(0xFF24357A))){Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){if(earned)Image(painterResource(R.drawable.hakum_mark_white),null,Modifier.size(24.dp)) else if(gift)Icon(Icons.Outlined.CardGiftcard,null,Modifier.size(18.dp),tint=Sky);Text(n.toString(),style=MaterialTheme.typography.labelSmall,color=Paper)}}
                };repeat(5-row.size){Spacer(Modifier.weight(1f))}
            }}
        }else LinearProgressIndicator(progress={progress.percent/100f},modifier=Modifier.fillMaxWidth(),color=Sky,trackColor=Color(0xFF24357A))
        HorizontalDivider(color=Color(0xFF24357A))
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Column(Modifier.weight(1f)){Text(data.customer.name,fontWeight=FontWeight.Bold);Text("Loyalty card",style=MaterialTheme.typography.labelSmall,color=Sky)};if(data.settings.pointsEnabled)Column(horizontalAlignment=Alignment.End){Text(data.account.points.toString(),fontWeight=FontWeight.Bold);Text("spend points",style=MaterialTheme.typography.labelSmall,color=Sky)}}
    }
}
val VisitSteps=listOf("pending","waiting","in_progress","final_checking","for_payment","completed")
fun statusLabel(status:String)=mapOf("pending" to "Booked","waiting" to "Queued","in_progress" to "In progress","final_checking" to "Final checking","for_payment" to "For payment","completed" to "Completed")[status]?:status
@Composable fun VisitCard(b:Booking,data:Snapshot,onClick:()->Unit){
    val vehicle=data.vehicles.find{it.id==b.vehicleId};val service=data.services.find{it.id==b.serviceId};val step=VisitSteps.indexOf(b.status).coerceAtLeast(0)
    Panel(navy=true){
        Text(DemoCatalog.branches[b.branch]?:b.branch,color=Sky,style=MaterialTheme.typography.labelSmall)
        Row(verticalAlignment=Alignment.CenterVertically){Text(vehicle?.plate?:"Vehicle",style=MaterialTheme.typography.headlineSmall,modifier=Modifier.weight(1f));Text("#${b.number.toString().padStart(3,'0')}",color=Sky,fontWeight=FontWeight.Bold)}
        Text("${service?.name} · ${vehicle?.make} ${vehicle?.model}",color=Sky,style=MaterialTheme.typography.bodyMedium)
        Row{Text(statusLabel(b.status),Modifier.weight(1f),fontWeight=FontWeight.Bold);Text("${step+1} / 6",color=Sky)}
        Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){VisitSteps.forEachIndexed{i,_->Box(Modifier.weight(1f).height(4.dp).background(if(i<=step)Sky else Color(0xFF24357A),CircleShape))}}
        Text("${b.date} · ${b.time}",style=MaterialTheme.typography.bodyMedium,color=Sky)
        OutlinedButton(onClick,border=BorderStroke(1.dp,Sky)){Text("See progress",color=Paper);Icon(Icons.AutoMirrored.Outlined.ArrowForward,null,tint=Sky)}
    }
}
