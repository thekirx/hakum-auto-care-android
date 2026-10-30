package com.hakum.customer.ui.vouchers

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.hakum.customer.CustomerViewModel
import com.hakum.customer.data.model.*
import com.hakum.customer.demo.DemoCatalog
import com.hakum.customer.domain.loyalty.LoyaltyEngine
import com.hakum.customer.ui.components.*
import java.time.Instant

@Composable fun VouchersScreen(data:Snapshot,vm:CustomerViewModel){
    var branch by rememberSaveable{mutableStateOf("bacoor")}
    var confirmation by remember{mutableStateOf<Pair<String,String>?>(null)}
    Page{Heading("Vouchers","Birthday wash and the points shop.");Choice("Pickup branch",DemoCatalog.branches.toList(),branch){branch=it}
        val birthday=data.vouchers.firstOrNull{it.kind=="birthday"&&it.year==vm.repository.today().year}
        Panel{Eyebrow("Your birthday gift");Text("Free carwash",style=MaterialTheme.typography.titleLarge)
            Muted(when{birthday?.used==true->"This year's birthday wash is already used.";birthday!=null->"Your code: ${birthday.code} · expires ${birthday.expiresAt.take(10)}";data.customer.birthday.isBlank()->"Add your birthday in Settings to unlock.";else->"Claim on your birthday. Valid for 7 days. One use this year."})
            if(birthday==null)Primary("Claim free car wash",LoyaltyEngine.birthdayToday(data.customer.birthday,vm.repository.today())){vm.action("Birthday pass ready"){vm.repository.claimBirthday()}}
            else Primary(if(birthday.used)"Used this year" else "Use birthday voucher",!birthday.used&&Instant.parse(birthday.expiresAt).isAfter(Instant.now())){confirmation=birthday.id to birthday.title}
        }
        Section("Points shop · ${data.account.points} points")
        data.rewards.filter{it.kind=="points"&&it.active}.forEach{r->
            val stock=if(branch=="bacoor")r.stockBacoor else r.stockBatangas
            val claimed=data.redemptions.any{it.rewardId==r.id}
            val enough=data.account.points>=r.pointsCost
            Panel{Text(r.label,style=MaterialTheme.typography.titleMedium);Muted("${r.pointsCost} points · ${if(stock>0)"$stock at this branch" else "out of stock"}")
                Primary(when{claimed->"Claimed";stock<=0->"Out of stock";!enough->"Need points";else->"Use points"},data.settings.pointsEnabled&&enough&&stock>0&&!claimed){confirmation=r.id to r.label}
            }
        }
        Section("Your redemptions")
        if(data.redemptions.isEmpty())Empty("No redemptions yet","Claimed rewards and used vouchers are saved here.")
        data.redemptions.sortedByDescending{it.createdAt}.forEach{r->Panel{Text(r.label,style=MaterialTheme.typography.titleMedium);Muted("${r.status} · ${DemoCatalog.branches[r.branch]}\n${r.createdAt.take(10)} · ${r.pointsSpent} points spent")}}
    }
    confirmation?.let{(id,title)->AlertDialog(onDismissRequest={confirmation=null},title={Text("Redeem $title?")},text={Text("Create a local redemption at ${DemoCatalog.branches[branch]}. This claim cannot be repeated in this demo.")},confirmButton={TextButton({confirmation=null;vm.action("Redemption saved"){if(data.vouchers.any{it.id==id})vm.repository.useVoucher(id,branch) else vm.repository.redeem(id,branch)}}){Text("Confirm redemption")}},dismissButton={TextButton({confirmation=null}){Text("Cancel")}})}
}
