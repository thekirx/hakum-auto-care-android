package com.hakum.customer.data.local

import androidx.room.*
import com.hakum.customer.data.model.*

@Dao interface HakumDao {
    @Query("SELECT * FROM Customer") suspend fun customers():List<Customer>
    @Query("SELECT * FROM Vehicle") suspend fun vehicles():List<Vehicle>
    @Query("SELECT * FROM Service") suspend fun services():List<Service>
    @Query("SELECT * FROM Booking") suspend fun bookings():List<Booking>
    @Query("SELECT * FROM Visit") suspend fun visits():List<Visit>
    @Query("SELECT * FROM LoyaltyAccount") suspend fun accounts():List<LoyaltyAccount>
    @Query("SELECT * FROM StampTransaction") suspend fun stamps():List<StampTransaction>
    @Query("SELECT * FROM PointsTransaction") suspend fun points():List<PointsTransaction>
    @Query("SELECT * FROM Reward") suspend fun rewards():List<Reward>
    @Query("SELECT * FROM Voucher") suspend fun vouchers():List<Voucher>
    @Query("SELECT * FROM MembershipTier") suspend fun memberships():List<MembershipTier>
    @Query("SELECT * FROM RedemptionRecord") suspend fun redemptions():List<RedemptionRecord>
    @Query("SELECT * FROM ProgramSettings") suspend fun settings():List<ProgramSettings>
    @Upsert suspend fun save(value:Customer)
    @Upsert suspend fun save(value:Vehicle)
    @Upsert suspend fun save(value:Service)
    @Upsert suspend fun save(value:Booking)
    @Upsert suspend fun save(value:Visit)
    @Upsert suspend fun save(value:LoyaltyAccount)
    @Upsert suspend fun save(value:StampTransaction)
    @Upsert suspend fun save(value:PointsTransaction)
    @Upsert suspend fun save(value:Reward)
    @Upsert suspend fun save(value:Voucher)
    @Upsert suspend fun save(value:MembershipTier)
    @Upsert suspend fun save(value:RedemptionRecord)
    @Upsert suspend fun save(value:ProgramSettings)
    @Query("DELETE FROM Customer") suspend fun clearCustomer()
    @Query("DELETE FROM Vehicle") suspend fun clearVehicle()
    @Query("DELETE FROM Service") suspend fun clearService()
    @Query("DELETE FROM Booking") suspend fun clearBooking()
    @Query("DELETE FROM Visit") suspend fun clearVisit()
    @Query("DELETE FROM LoyaltyAccount") suspend fun clearLoyaltyAccount()
    @Query("DELETE FROM StampTransaction") suspend fun clearStampTransaction()
    @Query("DELETE FROM PointsTransaction") suspend fun clearPointsTransaction()
    @Query("DELETE FROM Reward") suspend fun clearReward()
    @Query("DELETE FROM Voucher") suspend fun clearVoucher()
    @Query("DELETE FROM MembershipTier") suspend fun clearMembershipTier()
    @Query("DELETE FROM RedemptionRecord") suspend fun clearRedemptionRecord()
    @Query("DELETE FROM ProgramSettings") suspend fun clearProgramSettings()
}
@Database(entities=[Customer::class, Vehicle::class, Service::class, Booking::class, Visit::class, LoyaltyAccount::class, StampTransaction::class, PointsTransaction::class, Reward::class, Voucher::class, MembershipTier::class, RedemptionRecord::class, ProgramSettings::class],version=1,exportSchema=true)
abstract class HakumDatabase:RoomDatabase() { abstract fun dao():HakumDao }
