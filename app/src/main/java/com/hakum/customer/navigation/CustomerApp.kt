package com.hakum.customer.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.hakum.customer.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.app.Activity
import androidx.core.view.WindowCompat
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.hakum.customer.CustomerViewModel
import com.hakum.customer.ui.auth.AuthScreen
import com.hakum.customer.ui.home.HomeScreen
import com.hakum.customer.ui.queue.*
import com.hakum.customer.ui.booking.BookingScreen
import com.hakum.customer.ui.rewards.RewardsScreen
import com.hakum.customer.ui.vouchers.VouchersScreen
import com.hakum.customer.ui.profile.*
import com.hakum.customer.demo.DemoControlsScreen
import com.hakum.customer.ui.theme.HakumTheme

private data class Tab(val route:String,val title:String,val icon:ImageVector)
private val tabs=listOf(Tab("home","Home",Icons.Outlined.Home),Tab("queue","Queue",Icons.Outlined.GraphicEq),Tab("book","Book",Icons.Outlined.AddCircleOutline),Tab("rewards","Rewards",Icons.Outlined.CardGiftcard),Tab("me","Me",Icons.Outlined.PersonOutline))
@Composable fun CustomerApp(vm:CustomerViewModel){
    val state by vm.state.collectAsStateWithLifecycle()
    val snackbar=remember{SnackbarHostState()}
    LaunchedEffect(vm){vm.messages.collect{snackbar.showSnackbar(it)}}
    val view=LocalView.current
    SideEffect{val window=(view.context as Activity).window;WindowCompat.getInsetsController(window,view).apply{isAppearanceLightStatusBars=!state.preferences.dark;isAppearanceLightNavigationBars=!state.preferences.dark}}
    HakumTheme(state.preferences.dark){
        val data=state.data
        if(data==null){Scaffold(snackbarHost={SnackbarHost(snackbar)}){padding->Box(Modifier.fillMaxSize().padding(padding),contentAlignment=Alignment.Center){CircularProgressIndicator()}};return@HakumTheme}
        key(state.preferences.signedIn){
            val nav=rememberNavController()
            val entry by nav.currentBackStackEntryAsState()
            val route=entry?.destination?.route?:"home"
            val tab=when(route){"home","visit/{id}"->"home";"queue"->"queue";"book"->"book";"rewards","vouchers"->"rewards";else->"me"}
            fun go(destination:String){nav.navigate(destination){launchSingleTop=true}}
            Scaffold(
                snackbarHost={SnackbarHost(snackbar)},
                topBar={Surface(color=MaterialTheme.colorScheme.background){Row(Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal=20.dp,vertical=8.dp),verticalAlignment=Alignment.CenterVertically){
                    if(state.preferences.signedIn && route !in tabs.map{it.route})IconButton({nav.popBackStack()}){Icon(Icons.AutoMirrored.Outlined.ArrowBack,"Back")}
                    Image(painterResource(if(state.preferences.dark)R.drawable.hakum_lockup_white else R.drawable.hakum_lockup_blue),"Hakum Auto Care",Modifier.width(124.dp).height(36.dp));Spacer(Modifier.weight(1f));Text("AUTO CARE  /  DEMO",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary)
                }}},
                bottomBar={if(state.preferences.signedIn)Surface(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal=16.dp,vertical=10.dp),shape=RoundedCornerShape(30.dp),color=MaterialTheme.colorScheme.surface,border=BorderStroke(1.dp,MaterialTheme.colorScheme.outline.copy(alpha=.5f)),shadowElevation=12.dp){Row(Modifier.padding(horizontal=4.dp,vertical=6.dp)){
                    tabs.forEach{t->NavigationBarItem(selected=tab==t.route,onClick={nav.navigate(t.route){popUpTo(nav.graph.findStartDestination().id){saveState=true};launchSingleTop=true;restoreState=true}},icon={Icon(t.icon,null,Modifier.size(22.dp))},label={Text(t.title,style=MaterialTheme.typography.labelSmall)},modifier=Modifier.weight(1f),colors=NavigationBarItemDefaults.colors(indicatorColor=MaterialTheme.colorScheme.surfaceVariant))}
                }}}
            ){padding->Box(Modifier.fillMaxSize().padding(padding)){
                if(!state.preferences.signedIn)AuthScreen(vm) else NavHost(nav,"home"){
                    composable("home"){HomeScreen(data,::go)}
                    composable("queue"){QueueScreen(data,::go)}
                    composable("book"){BookingScreen(data,vm,::go)}
                    composable("rewards"){RewardsScreen(data,vm,::go)}
                    composable("me"){MeScreen(data,::go)}
                    composable("vouchers"){VouchersScreen(data,vm)}
                    composable("vehicles"){VehiclesScreen(data,vm)}
                    composable("history"){HistoryScreen(data)}
                    composable("settings"){SettingsScreen(data,state.preferences.dark,state.preferences.notifications,vm,::go)}
                    composable("demo"){DemoControlsScreen(data,vm,::go)}
                    composable("visit/{id}"){VisitScreen(it.arguments?.getString("id")?:"",data)}
                }
            }}
        }
    }
}
