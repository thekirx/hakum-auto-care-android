package com.hakum.customer.ui.rewards

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.hakum.customer.CustomerViewModel
import com.hakum.customer.data.model.*
import com.hakum.customer.domain.loyalty.*
import com.hakum.customer.ui.components.*

@Composable fun RewardsScreen(data:Snapshot,vm:CustomerViewModel,navigate:(String)->Unit){
    var claiming by remember{mutableStateOf<Reward?>(null)}
    Page{Heading("Rewards","A stamp for every paid visit.")
        if(data.settings.stampsEnabled)StampCard(data) else Empty("Stamps are paused","Completed visits still count toward your history.")
        Panel{Eyebrow("Member benefits");Text(LoyaltyEngine.tier(data.visits.size),style=MaterialTheme.typography.titleLarge);Muted("${data.visits.size} completed visits, any car")
            if(data.settings.membershipsEnabled && data.membership.active)Muted("${data.membership.name} · valid until ${data.membership.ends}")
            if(data.settings.pointsEnabled)Text("${data.account.points} loyalty points",style=MaterialTheme.typography.titleMedium)
        }
        Section("Your rewards")
        data.rewards.filter{it.kind!="points" && it.active}.forEach{r->
            val claimed=data.redemptions.any{it.rewardId==r.id}
            val balance=if(r.kind=="stamp")data.account.stamps else data.visits.size
            val enabled=(r.kind!="stamp"||data.settings.stampsEnabled)&&balance>=r.threshold&&!claimed
            Panel{Text(r.label,style=MaterialTheme.typography.titleMedium);Muted(if(claimed)"Claimed · saved in your redemptions" else if(balance>=r.threshold)"Earned · ready to claim" else "${(r.threshold-balance).coerceAtLeast(0)} more ${if(r.kind=="stamp")"stamps" else "visits"}")
                if(r.kind=="stamp")Muted("${r.threshold} stamps to claim · stamp balance is reduced on redemption")
                Primary(if(claimed)"Already claimed" else "Claim reward",enabled){claiming=r}
            }
        }
        MenuRow("Vouchers & points shop","Birthday wash, accessories and your redemptions"){navigate("vouchers")}
        var showTable by rememberSaveable{mutableStateOf(false)}
        TextButton({showTable=!showTable}){Text(if(showTable)"Hide points table" else "View points table")}
        if(showTable)Panel{data.services.forEach{Text("${it.name} · ${it.points} points")};Muted("Service awards follow the source catalog. Paid membership multipliers apply only while eligible.")}
    }
    claiming?.let{r->AlertDialog(onDismissRequest={claiming=null},title={Text("Claim ${r.label}?")},text={Text(if(r.kind=="stamp")"This uses ${r.threshold} stamps. A local pickup record will be created at Bacoor." else "This visit reward can be claimed once in this demo.")},confirmButton={TextButton({claiming=null;vm.action("Reward claimed"){vm.repository.redeem(r.id,"bacoor")}}){Text("Confirm claim")}},dismissButton={TextButton({claiming=null}){Text("Cancel")}})}
}
