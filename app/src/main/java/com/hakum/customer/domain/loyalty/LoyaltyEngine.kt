package com.hakum.customer.domain.loyalty

import java.time.LocalDate
import kotlin.math.floor
import kotlin.math.roundToInt

data class StampRules(val enabled:Boolean=true, val mode:String="all_weighted", val categories:Set<String> = setOf("wash"), val multiply:Boolean=false, val memberships:Boolean=true)
data class Milestone(val threshold:Int,val label:String,val active:Boolean=true)
data class Progress(val slots:Int,val completed:Int,val percent:Int,val next:Milestone?,val earned:List<Milestone>)
data class Credits(val stamps:Int,val points:Int)

/** JS helpers are kept distinct from the later SQL visit/catalog award policy. */
object LoyaltyEngine {
    private fun finite(multiplier:Double)=if(multiplier.isFinite())multiplier else 1.0
    fun stampDelta(rules:StampRules,weight:Double,category:String,quantity:Int,multiplier:Double=1.0,loyaltyAward:Boolean=false):Int {
        if(!rules.enabled || loyaltyAward || !weight.isFinite() || weight<1)return 0
        if(rules.mode=="pay_categories" && category !in rules.categories)return 0
        val base=floor(weight).toInt()*quantity.coerceAtLeast(1)
        return if(rules.multiply && rules.memberships)floor(base*finite(multiplier)).toInt().coerceAtLeast(0) else base
    }
    fun eligibleMultiplier(multiplier:Double,active:Boolean,ends:LocalDate?,today:LocalDate):Double = if(active && (ends==null || !ends.isBefore(today)))finite(multiplier) else 1.0
    fun spendPoints(minor:Long,multiplier:Double=1.0):Int = if(minor<=0)0 else floor(minor/100.0*finite(multiplier)).toInt().coerceAtLeast(0)
    fun servicePoints(points:Int,quantity:Int=1,multiplier:Double=1.0):Int = floor(points.coerceAtLeast(0)*quantity.coerceAtLeast(1)*finite(multiplier)).toInt().coerceAtLeast(0)
    fun progress(stamps:Int,slots:Int,milestones:List<Milestone>):Progress {
        val count=(if(slots==0)15 else slots).coerceAtLeast(1);val completed=stamps.coerceIn(0,count);val active=milestones.filter{it.active}.sortedBy{it.threshold}
        return Progress(count,completed,(completed*100.0/count).roundToInt(),active.firstOrNull{completed<it.threshold},active.filter{completed>=it.threshold})
    }
    fun wrap(stamps:Int,slots:Int,enabled:Boolean):Int {val n=stamps.coerceAtLeast(0);return if(enabled && slots>0 && n>=slots)n%slots else n}
    fun defaultPointsAward(slug:String,name:String,category:String):Int {
        val hay="$slug $name".lowercase()
        return when {
            Regex("ppf|paint-protection|paint protection").containsMatchIn(hay)->10
            "tint" in hay->3
            "ceramic" in hay->5
            "express" in hay->2
            category.lowercase()=="wash" || Regex("carwash|car-wash|car wash").containsMatchIn(hay)->1
            category.lowercase()=="detailing"->3
            else->0
        }
    }
    fun tier(visits:Int):String = "Hakum "+when {visits>=100->"Platinum";visits>=70->"Premium";visits>=30->"Deluxe";else->"Basic"}+" Member"
    fun redeem(balance:Int,cost:Int,stock:Int,alreadyClaimed:Boolean):Int {
        require(!alreadyClaimed){"Already claimed"};require(cost>0){"Invalid points cost"};require(stock>0){"Out of stock at this branch"};require(balance>=cost){"Not enough points"};return balance-cost
    }
    fun visitCredits(status:String,paid:Boolean,credited:Boolean,points:Int,stampsEnabled:Boolean=true,pointsEnabled:Boolean=true,multiplier:Double=1.0):Credits {
        if(status!="completed" || !paid || credited)return Credits(0,0)
        return Credits(if(stampsEnabled)1 else 0,if(pointsEnabled)servicePoints(points,1,multiplier) else 0)
    }
    fun advance(status:String):String {val steps=listOf("pending","waiting","in_progress","final_checking","for_payment","completed");val i=steps.indexOf(status);return if(i<0)status else steps[(i+1).coerceAtMost(steps.lastIndex)]}
    fun birthdayCode(random:kotlin.random.Random=kotlin.random.Random.Default):String {
        val alphabet="ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return "BD"+(1..4).map{alphabet[random.nextInt(alphabet.length)]}.joinToString("")
    }
    fun birthdayToday(dob:String,today:LocalDate):Boolean {
        val birth=runCatching{LocalDate.parse(dob)}.getOrNull()?:return false
        return birth.month==today.month && (birth.dayOfMonth==today.dayOfMonth || (birth.monthValue==2 && birth.dayOfMonth==29 && !today.isLeapYear && today.dayOfMonth==28))
    }
}
