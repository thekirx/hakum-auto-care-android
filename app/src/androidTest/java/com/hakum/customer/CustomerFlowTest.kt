package com.hakum.customer

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CustomerFlowTest {
    @get:Rule val compose=createAndroidComposeRule<MainActivity>()
    private fun click(text:String){compose.waitUntil(10000){compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()};compose.onNodeWithText(text).performScrollTo().performClick();compose.waitForIdle()}
    private fun tab(text:String){compose.onNodeWithText(text).performClick();compose.waitForIdle()}
    @Test fun offlineCustomerCanBookNavigateAndEarnLoyalty() {
        compose.waitUntil(20000){compose.onAllNodesWithText("EXPLORE DEMO ACCOUNT").fetchSemanticsNodes().isNotEmpty() || compose.onAllNodesWithText("Home").fetchSemanticsNodes().isNotEmpty()}
        if(compose.onAllNodesWithText("EXPLORE DEMO ACCOUNT").fetchSemanticsNodes().isNotEmpty())click("EXPLORE DEMO ACCOUNT")
        compose.waitUntil(10000){compose.onAllNodesWithText("Home").fetchSemanticsNodes().isNotEmpty()}
        tab("Queue");compose.onNodeWithText("LIVE QUEUE").assertExists()
        tab("Book")
        compose.onNodeWithText("REVIEW BOOKING").performScrollTo() // bring date/time into view
        val date=LocalDate.now(ZoneId.of("Asia/Manila")).plusDays(2).format(DateTimeFormatter.ofPattern("EEE, MMM d"))
        click(date);click("09:00");click("REVIEW BOOKING")
        compose.onNodeWithText("Confirm booking").performClick()
        compose.waitUntil(10000){compose.onAllNodesWithText("YOUR VISIT").fetchSemanticsNodes().isNotEmpty()}
        tab("Rewards");compose.onNodeWithText("7 of 15 stamps").assertExists()
        compose.waitUntil(10000){compose.onAllNodesWithText("Booking saved").fetchSemanticsNodes().isEmpty()}
        tab("Me");click("Profile & settings");click("Demo Controls")
        click("SIMULATE COMPLETED PAID SERVICE")
        tab("Rewards")
        compose.waitUntil(10000){compose.onAllNodesWithText("8 of 15 stamps").fetchSemanticsNodes().isNotEmpty()}
        tab("Home");compose.onNodeWithText("HI, ALEX.").assertExists()
    }
}
