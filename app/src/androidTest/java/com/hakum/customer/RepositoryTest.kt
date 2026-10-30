package com.hakum.customer

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.room.Room
import com.hakum.customer.data.local.HakumDatabase
import com.hakum.customer.data.repository.CustomerRepository
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class RepositoryTest {
    @Test fun bookingPaymentCreditsRedemptionAndResetAreAtomic() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context,HakumDatabase::class.java).build()
        try {
            val repo = CustomerRepository(db)
            repo.seed()
            val first = repo.snapshot()
            val booking = repo.book("bacoor",first.vehicles.first().id,"express",LocalDate.now().plusDays(2).toString(),"10:00")
            assertTrue(repo.snapshot().bookings.any {it.id==booking})
            repeat(5) { repo.advance(booking) }
            val completed = repo.snapshot()
            assertEquals("completed",completed.bookings.first {it.id==booking}.status)
            assertEquals(first.account.stamps+1,completed.account.stamps)
            assertEquals(first.account.points+2,completed.account.points)
            repo.complete(booking)
            assertEquals(completed.account,repo.snapshot().account)
            repo.redeem("merch-key", "bacoor")
            val redeemed = repo.snapshot()
            assertEquals(completed.account.points-20,redeemed.account.points)
            assertTrue(runCatching {repo.redeem("merch-key","bacoor")}.isFailure)
            repo.reset()
            assertEquals(first.account,repo.snapshot().account)
            assertTrue(repo.snapshot().redemptions.isEmpty())
        } finally { db.close() }
    }
    @Test fun concurrentClaimsAndExpiredBirthdayAreRejected() = runBlocking {
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        val db=Room.inMemoryDatabaseBuilder(context,HakumDatabase::class.java).build()
        try {
            val repo=CustomerRepository(db);repo.seed()
            val attempts=coroutineScope {(1..2).map{async{runCatching{repo.redeem("merch-key","bacoor")}.isSuccess}}.awaitAll()}
            assertEquals(1,attempts.count{it})
            repo.demoBirthdayToday();repo.claimBirthday()
            assertTrue(runCatching{repo.claimBirthday()}.isFailure)
            val voucher=repo.snapshot().vouchers.first()
            db.dao().save(voucher.copy(expiresAt="2000-01-01T00:00:00Z"))
            assertTrue(runCatching{repo.useVoucher(voucher.id,"bacoor")}.isFailure)
        } finally {db.close()}
    }
    @Test fun weightedConfigurationChangesAwards() = runBlocking {
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        val db=Room.inMemoryDatabaseBuilder(context,HakumDatabase::class.java).build()
        try {
            val repo=CustomerRepository(db);repo.seed()
            val before=repo.snapshot().account
            repo.configure(repo.snapshot().settings.copy(earnMode="pay_categories",payCategories="detailing",multiplyStamps=true))
            repo.configureService("express",2);repo.setMembership(true)
            repo.simulateService("express")
            assertEquals(before.stamps+3,repo.snapshot().account.stamps)
            assertEquals(before.points+3,repo.snapshot().account.points)
        } finally {db.close()}
    }
    @Test fun fileDatabaseRetainsChangesAfterReopen() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "persistence-test.db"
        context.deleteDatabase(name)
        var db = Room.databaseBuilder(context,HakumDatabase::class.java,name).build()
        try {
            val repo = CustomerRepository(db); repo.seed()
            val id = repo.book("batangas",repo.snapshot().vehicles.first().id,"express",LocalDate.now().plusDays(3).toString(),"11:00")
            db.close()
            db = Room.databaseBuilder(context,HakumDatabase::class.java,name).build()
            assertTrue(CustomerRepository(db).snapshot().bookings.any {it.id==id})
        } finally {db.close();context.deleteDatabase(name)}
    }
}
