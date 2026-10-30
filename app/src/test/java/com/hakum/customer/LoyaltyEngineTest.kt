package com.hakum.customer

import com.hakum.customer.domain.loyalty.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class LoyaltyEngineTest {
    @Test fun weightedStampsRespectCategoryAndQuantity() {
        assertEquals(6, LoyaltyEngine.stampDelta(StampRules(), 2.9, "wash", 3))
        assertEquals(0, LoyaltyEngine.stampDelta(StampRules(mode="pay_categories"), 2.0, "detailing", 1))
        assertEquals(0, LoyaltyEngine.stampDelta(StampRules(enabled=false), 2.0, "wash", 1))
        assertEquals(0, LoyaltyEngine.stampDelta(StampRules(), 1.0, "wash", 1, loyaltyAward=true))
    }
    @Test fun multiplierAppliesOnlyWhenEnabledAndEligible() {
        assertEquals(3, LoyaltyEngine.stampDelta(StampRules(multiply=true), 2.0, "wash", 1, 1.5))
        assertEquals(2, LoyaltyEngine.stampDelta(StampRules(multiply=true, memberships=false), 2.0, "wash", 1, 1.5))
        assertEquals(1.0, LoyaltyEngine.eligibleMultiplier(2.0, false, null, LocalDate.now()), 0.0)
        assertEquals(1.0, LoyaltyEngine.eligibleMultiplier(2.0, true, LocalDate.now().minusDays(1), LocalDate.now()), 0.0)
    }
    @Test fun spendAndCatalogPointsUseDistinctSourceRules() {
        assertEquals(376, LoyaltyEngine.spendPoints(25099, 1.5))
        assertEquals(22, LoyaltyEngine.servicePoints(15, 1, 1.5))
        assertEquals(0, LoyaltyEngine.spendPoints(-100))
    }
    @Test fun progressUsesConfiguredSlotsAndActiveMilestones() {
        val p = LoyaltyEngine.progress(7, 12, listOf(Milestone(6,"Coffee"), Milestone(9,"Wash"), Milestone(8,"Off",false)))
        assertEquals(58, p.percent); assertEquals(9, p.next!!.threshold); assertEquals(1,p.earned.size)
        assertEquals(12,LoyaltyEngine.progress(99,12,emptyList()).completed)
        assertEquals(2,LoyaltyEngine.wrap(17,15,true))
        assertEquals(17,LoyaltyEngine.wrap(17,15,false))
    }
    @Test fun defaultServicePointsFollowSourceFallbackOrder() {
        assertEquals(10,LoyaltyEngine.defaultPointsAward("paint-protection","Ceramic PPF","detailing"))
        assertEquals(3,LoyaltyEngine.defaultPointsAward("nano-tint","Tint","detailing"))
        assertEquals(5,LoyaltyEngine.defaultPointsAward("ceramic","Ceramic","detailing"))
        assertEquals(2,LoyaltyEngine.defaultPointsAward("express","Express","detailing"))
        assertEquals(1,LoyaltyEngine.defaultPointsAward("car-wash","Wash","wash"))
        assertEquals(3,LoyaltyEngine.defaultPointsAward("basic","Basic","detailing"))
        assertEquals(0,LoyaltyEngine.defaultPointsAward("unknown","Unknown","general"))
    }
    @Test fun tiersFollowVisitThresholds() {
        assertEquals("Hakum Basic Member",LoyaltyEngine.tier(29))
        assertEquals("Hakum Deluxe Member",LoyaltyEngine.tier(30))
        assertEquals("Hakum Premium Member",LoyaltyEngine.tier(70))
        assertEquals("Hakum Platinum Member",LoyaltyEngine.tier(100))
    }
    @Test fun redemptionRejectsInsufficientBalanceStockAndRepeatedClaim() {
        assertEquals(10,LoyaltyEngine.redeem(30,20,1,false))
        for (args in listOf(Triple(10,1,false), Triple(30,0,false),Triple(30,1,true))) {
            assertTrue(runCatching { LoyaltyEngine.redeem(args.first,20,args.second,args.third) }.isFailure)
        }
    }
    @Test fun lifecycleRequiresPaidCompletedServiceAndPreventsDuplicateCredits() {
        assertEquals(Credits(1,15), LoyaltyEngine.visitCredits("completed",true,false,15))
        assertEquals(Credits(0,0), LoyaltyEngine.visitCredits("completed",true,true,15))
        assertEquals(Credits(0,0), LoyaltyEngine.visitCredits("in_progress",true,false,15))
        assertEquals(Credits(0,0), LoyaltyEngine.visitCredits("completed",false,false,15))
        assertEquals("waiting",LoyaltyEngine.advance("pending"))
        assertEquals("completed",LoyaltyEngine.advance("for_payment"))
        assertEquals("completed",LoyaltyEngine.advance("completed"))
    }
    @Test fun futurePendingBookingsDoNotInflateFloorCounts() {
        val b=com.hakum.customer.data.model.Booking("scheduled","bacoor","car","wash","2099-01-01","10:00")
        val snapshot=com.hakum.customer.data.model.Snapshot(
            com.hakum.customer.data.model.Customer(name="Demo",email="a@example.test",phone="09170000000",birthday=""),
            emptyList(),emptyList(),listOf(b),emptyList(),com.hakum.customer.data.model.LoyaltyAccount(),emptyList(),emptyList(),emptyList(),emptyList(),com.hakum.customer.data.model.MembershipTier(),emptyList(),com.hakum.customer.data.model.ProgramSettings())
        assertEquals(0,snapshot.queue("bacoor").waiting)
        assertEquals(1,snapshot.copy(bookings=listOf(b.copy(status="waiting"))).queue("bacoor").waiting)
    }
    @Test fun birthdayCodeUsesUnambiguousSourceAlphabet() {
        val random=kotlin.random.Random(7)
        repeat(100){assertTrue(LoyaltyEngine.birthdayCode(random).matches(Regex("BD[A-HJ-NP-Z2-9]{4}")))}
    }
    @Test fun birthdaySupportsLeapDayAndOncePerYear() {
        val day = LocalDate.of(2026,2,28)
        assertTrue(LoyaltyEngine.birthdayToday("2000-02-29",day))
        assertFalse(LoyaltyEngine.birthdayToday("2000-03-01",day))
    }
}
