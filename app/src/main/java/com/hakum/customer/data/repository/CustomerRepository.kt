package com.hakum.customer.data.repository

import androidx.room.withTransaction
import com.hakum.customer.data.local.HakumDatabase
import com.hakum.customer.data.model.*
import com.hakum.customer.demo.DemoCatalog
import com.hakum.customer.domain.loyalty.*
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.time.*
import java.util.UUID

class CustomerRepository(private val db:HakumDatabase) {
    private val dao=db.dao()
    private val tables=arrayOf("Customer","Vehicle","Service","Booking","Visit","LoyaltyAccount","StampTransaction","PointsTransaction","Reward","Voucher","MembershipTier","RedemptionRecord","ProgramSettings")
    val changes=db.invalidationTracker.createFlow(*tables).map { if(dao.customers().isEmpty()) null else snapshot() }
    fun today():LocalDate=LocalDate.now(ZoneId.of("Asia/Manila"))
    private fun now()=Instant.now().toString()
    suspend fun snapshot():Snapshot=db.withTransaction {
        Snapshot(dao.customers().first(),dao.vehicles(),dao.services(),dao.bookings().sortedByDescending{it.number},dao.visits().sortedByDescending{it.date},dao.accounts().first(),dao.stamps(),dao.points().sortedByDescending{it.createdAt},dao.rewards(),dao.vouchers(),dao.memberships().first(),dao.redemptions(),dao.settings().first())
    }
    suspend fun seed()=db.withTransaction {if(dao.customers().isEmpty())seedData()}
    private suspend fun seedData() {
        dao.save(Customer(name="Alex Rivera",email="alex@example.test",phone="09170000000",birthday="1996-11-16",passwordHash=hash("hakumdemo")))
        dao.save(Vehicle("car-1","DEMO 101","Toyota","Corolla Cross"))
        DemoCatalog.services.forEach{dao.save(it)}; DemoCatalog.rewards.forEach{dao.save(it)}
        dao.save(LoyaltyAccount(stamps=7,points=DemoCatalog.services.take(7).sumOf{it.points}));dao.save(MembershipTier());dao.save(ProgramSettings())
        DemoCatalog.services.take(7).forEachIndexed {i,s->
            val id="history-$i";val date=today().minusDays((i+1)*7L).toString()
            dao.save(Visit(id,id,s.name,"DEMO 101","bacoor",date,s.priceMinor))
            dao.save(StampTransaction(id,1,date));dao.save(PointsTransaction(id,s.points,s.name,date))
        }
        dao.save(Booking("active-1","bacoor","car-1","wash",today().toString(),"10:00","in_progress",number=8))
    }
    suspend fun reset()=db.withTransaction {
        dao.clearCustomer()
        dao.clearVehicle()
        dao.clearService()
        dao.clearBooking()
        dao.clearVisit()
        dao.clearLoyaltyAccount()
        dao.clearStampTransaction()
        dao.clearPointsTransaction()
        dao.clearReward()
        dao.clearVoucher()
        dao.clearMembershipTier()
        dao.clearRedemptionRecord()
        dao.clearProgramSettings()
        seedData()
    }
    suspend fun book(branch:String,vehicle:String,service:String,date:String,time:String):String=db.withTransaction {
        require(DemoCatalog.branches.containsKey(branch)){"Choose a branch"}
        require(dao.vehicles().any{it.id==vehicle}){"Choose a saved vehicle"}
        require(dao.services().any{it.id==service}){"Choose a service"}
        require(!LocalDate.parse(date).isBefore(today().plusDays(2))){"Bookings open two days ahead"}
        require(time in listOf("09:00","10:00","11:00","13:00","14:00","15:00","16:00")){"Choose an available time"}
        require(dao.bookings().none{it.vehicleId==vehicle && it.date==date && it.time==time && it.status!="cancelled"}){"This vehicle already has a booking at that time"}
        val id=UUID.randomUUID().toString()
        dao.save(Booking(id,branch,vehicle,service,date,time,number=(dao.bookings().maxOfOrNull{it.number}?:0)+1));id
    }
    suspend fun advance(id:String)=db.withTransaction {
        val b=dao.bookings().first{it.id==id}
        if(LoyaltyEngine.advance(b.status)=="completed")complete(id)
        else dao.save(b.copy(status=LoyaltyEngine.advance(b.status)))
    }
    suspend fun complete(id:String)=db.withTransaction {
        val b=dao.bookings().first{it.id==id}
        if(dao.stamps().any{it.visitId==id})return@withTransaction
        val service=dao.services().first{it.id==b.serviceId};val vehicle=dao.vehicles().first{it.id==b.vehicleId}
        val config=dao.settings().first();val account=dao.accounts().first();val member=dao.memberships().first()
        val multiplier=LoyaltyEngine.eligibleMultiplier(member.multiplier,member.active && config.membershipsEnabled,member.ends.takeIf{it.isNotEmpty()}?.let{LocalDate.parse(it)},today())
        val credit=LoyaltyEngine.visitCredits("completed",true,false,service.points,config.stampsEnabled,config.pointsEnabled,multiplier)
        val stamps=if(config.earnMode=="visit")credit.stamps else LoyaltyEngine.stampDelta(StampRules(config.stampsEnabled,config.earnMode,config.payCategories.split(",").toSet(),config.multiplyStamps,config.membershipsEnabled),service.weight.toDouble(),service.category,1,multiplier)
        dao.save(b.copy(status="completed",paid=true))
        dao.save(Visit(id,id,service.name,vehicle.plate,b.branch,today().toString(),service.priceMinor))
        dao.save(StampTransaction(id,stamps,now()))
        dao.save(PointsTransaction(id,credit.points,service.name,now()))
        dao.save(account.copy(stamps=LoyaltyEngine.wrap(account.stamps+stamps,config.slots,config.wrap),points=account.points+credit.points))
    }
    suspend fun simulateService(serviceId:String) {
        val s=snapshot();require(s.vehicles.isNotEmpty()){"Add a vehicle first"}
        val dates=(2L..365L).map{today().plusDays(it).toString()}
        val slot=dates.flatMap{date->listOf("09:00","10:00","11:00","13:00","14:00","15:00","16:00").map{date to it}}.first{(date,time)->s.bookings.none{it.vehicleId==s.vehicles.first().id && it.date==date && it.time==time}}
        val id=book("bacoor",s.vehicles.first().id,serviceId,slot.first,slot.second)
        complete(id)
    }
    suspend fun redeem(id:String,branch:String)=db.withTransaction {
        require(branch in DemoCatalog.branches){"Select a pickup branch"}
        val reward=dao.rewards().first{it.id==id};val account=dao.accounts().first();val config=dao.settings().first()
        require(reward.active){"Reward is inactive"}
        require(dao.redemptions().none{it.rewardId==id}){"Already claimed"}
        when(reward.kind) {
            "points"->{
                require(config.pointsEnabled){"Points are paused"}
                val stock=if(branch=="bacoor")reward.stockBacoor else reward.stockBatangas
                val balance=LoyaltyEngine.redeem(account.points,reward.pointsCost,stock,false)
                dao.save(account.copy(points=balance))
                dao.save(if(branch=="bacoor")reward.copy(stockBacoor=stock-1) else reward.copy(stockBatangas=stock-1))
                dao.save(PointsTransaction("redeem-$id",-reward.pointsCost,reward.label,now()))
            }
            "stamp"->{require(config.stampsEnabled && account.stamps>=reward.threshold){"Not enough stamps"};dao.save(account.copy(stamps=account.stamps-reward.threshold));dao.save(StampTransaction("redeem-$id",-reward.threshold,now()))}
            "visit"->require(dao.visits().size>=reward.threshold){"Not enough completed visits"}
        }
        dao.save(RedemptionRecord(id,reward.label,reward.kind,reward.pointsCost,branch,now()))
    }
    suspend fun claimBirthday()=db.withTransaction {
        val customer=dao.customers().first();val date=today()
        require(LoyaltyEngine.birthdayToday(customer.birthday,date)){"The free wash opens on your birthday"}
        require(dao.vouchers().none{it.kind=="birthday" && it.year==date.year}){"Already claimed this year"}
        dao.save(Voucher("birthday-${date.year}","Free carwash",LoyaltyEngine.birthdayCode(),"birthday",date.year,Instant.now().plusSeconds(7*86400).toString()))
    }
    suspend fun useVoucher(id:String,branch:String)=db.withTransaction {
        val v=dao.vouchers().first{it.id==id}
        require(!v.used){"Voucher already used"};require(v.expiresAt.isEmpty() || Instant.parse(v.expiresAt).isAfter(Instant.now())){"Voucher expired"}
        require(branch in DemoCatalog.branches){"Select a branch"}
        dao.save(v.copy(used=true));dao.save(RedemptionRecord(id,v.title,"voucher",0,branch,now(),"Used"))
    }
    suspend fun updateProfile(name:String,phone:String,birthday:String)=db.withTransaction {
        require(name.trim().length>=2 && phone.count{it.isDigit()}>=10){"Enter a name and valid phone"}
        val c=dao.customers().first()
        require(c.birthday.isBlank() || c.birthday==birthday){"Birthday is locked after the first save"}
        if(birthday.isNotEmpty())require(!LocalDate.parse(birthday).isAfter(today())){"Birthday must be in the past"}
        dao.save(c.copy(name=name.trim(),phone=phone,birthday=birthday))
    }
    suspend fun addVehicle(plate:String,make:String,model:String)=db.withTransaction {
        val normalized=plate.trim().uppercase()
        require(normalized.length in 4..12 && normalized.any{it.isLetter()} && normalized.any{it.isDigit()} && make.isNotBlank() && model.isNotBlank()){"Enter plate, make and model"}
        require(dao.vehicles().none{it.plate==normalized}){"Plate already saved"}
        dao.save(Vehicle(UUID.randomUUID().toString(),normalized,make.trim(),model.trim()))
    }
    suspend fun register(name:String,email:String,phone:String,password:String,birthday:String)=db.withTransaction {
        require(name.trim().length>=2 && email.contains("@") && phone.count{it.isDigit()}>=10 && password.length>=8){"Enter your name, email, phone and an 8-character password"}
        if(birthday.isNotBlank())require(!LocalDate.parse(birthday).isAfter(today())){"Birthday must be in the past"}
        // A single local customer sandbox: sign-up starts a clean customer, never mixes seed history.
        dao.clearCustomer()
        dao.clearVehicle()
        dao.clearBooking()
        dao.clearVisit()
        dao.clearLoyaltyAccount()
        dao.clearStampTransaction()
        dao.clearPointsTransaction()
        dao.clearVoucher()
        dao.clearMembershipTier()
        dao.clearRedemptionRecord()
        dao.save(Customer(name=name.trim(),email=email.trim(),phone=phone,birthday=birthday,passwordHash=hash(password)))
        dao.save(LoyaltyAccount());dao.save(MembershipTier())
        DemoCatalog.rewards.forEach{dao.save(it)}
    }
    suspend fun signIn(email:String,password:String):Boolean {val c=dao.customers().first();return c.email.equals(email.trim(),true)&&c.passwordHash==hash(password)}
    suspend fun demoLogin() {if(dao.customers().first().email!="alex@example.test")reset()}
    suspend fun sampleHistory()=db.withTransaction {
        val account=dao.accounts().first();var count=0
        repeat(5) { i->
            val id="sample-$i"
            if(dao.visits().none{it.id==id}){
                val date=today().minusDays(i+1L).toString();dao.save(Visit(id,id,"Premium car wash","DEMO 101","bacoor",date,35000));dao.save(StampTransaction(id,1,now()));dao.save(PointsTransaction(id,1,"Sample visit",now()));count++
            }
        }
        dao.save(account.copy(stamps=account.stamps+count,points=account.points+count))
    }
    suspend fun configure(settings:ProgramSettings)=db.withTransaction {require(settings.slots in 5..50){"Choose 5–50 slots"};dao.save(settings)}
    suspend fun configureService(id:String,weight:Int)=db.withTransaction {require(weight in 0..10){"Choose weight 0–10"};dao.save(dao.services().first{it.id==id}.copy(weight=weight))}
    suspend fun setMembership(active:Boolean)=db.withTransaction {dao.save(MembershipTier(name=if(active)"Demo membership ×1.5" else "No paid membership",active=active,multiplier=if(active)1.5 else 1.0,ends=today().plusDays(30).toString()))}
    suspend fun demoBirthdayToday()=db.withTransaction {val c=dao.customers().first();dao.save(c.copy(birthday=LocalDate.of(2000,today().monthValue,today().dayOfMonth).toString()))}
    suspend fun clearHistory()=db.withTransaction {dao.clearVisit()}
    companion object { fun hash(value:String)=MessageDigest.getInstance("SHA-256").digest(value.toByteArray()).joinToString(""){"%02x".format(it)} }
}
