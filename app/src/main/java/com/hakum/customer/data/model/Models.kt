package com.hakum.customer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity data class Customer(@PrimaryKey val id:Int=1,val name:String,val email:String,val phone:String,val birthday:String,val passwordHash:String="")
@Entity data class Vehicle(@PrimaryKey val id:String,val plate:String,val make:String,val model:String,val color:String="White",val size:String="medium")
@Entity data class Service(@PrimaryKey val id:String,val name:String,val category:String,val priceMinor:Long,val points:Int,val weight:Int=1,val description:String="")
@Entity data class Booking(@PrimaryKey val id:String,val branch:String,val vehicleId:String,val serviceId:String,val date:String,val time:String,val status:String="pending",val paid:Boolean=false,val number:Int=1)
@Entity data class Visit(@PrimaryKey val id:String,val bookingId:String,val service:String,val plate:String,val branch:String,val date:String,val amountMinor:Long)
@Entity data class LoyaltyAccount(@PrimaryKey val id:Int=1,val stamps:Int=0,val points:Int=0)
@Entity data class StampTransaction(@PrimaryKey val visitId:String,val delta:Int,val createdAt:String)
@Entity data class PointsTransaction(@PrimaryKey val id:String,val delta:Int,val reason:String,val createdAt:String)
@Entity data class Reward(@PrimaryKey val id:String,val label:String,val threshold:Int=0,val pointsCost:Int=0,val kind:String="stamp",val active:Boolean=true,val stockBacoor:Int=1,val stockBatangas:Int=1)
@Entity data class Voucher(@PrimaryKey val id:String,val title:String,val code:String,val kind:String,val year:Int=0,val expiresAt:String="",val used:Boolean=false,val category:String="all")
@Entity data class MembershipTier(@PrimaryKey val id:Int=1,val name:String="No paid membership",val multiplier:Double=1.0,val active:Boolean=false,val ends:String="")
@Entity data class RedemptionRecord(@PrimaryKey val rewardId:String,val label:String,val kind:String,val pointsSpent:Int,val branch:String,val createdAt:String,val status:String="Ready for pickup")
@Entity data class ProgramSettings(@PrimaryKey val id:Int=1,val slots:Int=15,val stampsEnabled:Boolean=true,val pointsEnabled:Boolean=true,val membershipsEnabled:Boolean=true,val wrap:Boolean=false,val earnMode:String="visit",val multiplyStamps:Boolean=false,val payCategories:String="wash")

data class QueueStatus(val waiting:Int,val inProgress:Int,val checking:Int)
data class Snapshot(val customer:Customer,val vehicles:List<Vehicle>,val services:List<Service>,val bookings:List<Booking>,val visits:List<Visit>,val account:LoyaltyAccount,val stamps:List<StampTransaction>,val points:List<PointsTransaction>,val rewards:List<Reward>,val vouchers:List<Voucher>,val membership:MembershipTier,val redemptions:List<RedemptionRecord>,val settings:ProgramSettings) {
    val active get()=bookings.filter {it.status!="completed" && it.status!="cancelled"}
    fun queue(branch:String)=QueueStatus(active.count{it.branch==branch && it.status=="waiting"},active.count{it.branch==branch && it.status=="in_progress"},active.count{it.branch==branch && it.status=="final_checking"})
}
