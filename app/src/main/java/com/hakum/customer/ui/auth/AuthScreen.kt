package com.hakum.customer.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.hakum.customer.CustomerViewModel
import com.hakum.customer.ui.components.*

@Composable fun AuthScreen(vm:CustomerViewModel){
    var signup by rememberSaveable{mutableStateOf(false)}
    var email by rememberSaveable{mutableStateOf("")};var password by rememberSaveable{mutableStateOf("")}
    var name by rememberSaveable{mutableStateOf("")};var phone by rememberSaveable{mutableStateOf("")};var birthday by rememberSaveable{mutableStateOf("")}
    Page{
        Spacer(Modifier.height(28.dp));Eyebrow("Hakum Auto Care");Heading(if(signup)"Join the club." else "Care for your car.\nRewards for you.","Your garage, visits and rewards, in one place.")
        Panel{
            Text(if(signup)"Create a local account" else "Welcome back",style=MaterialTheme.typography.titleLarge)
            if(signup){OutlinedTextField(name,{name=it},label={Text("Full name")},modifier=Modifier.fillMaxWidth(),singleLine=true);OutlinedTextField(phone,{phone=it},label={Text("Phone")},modifier=Modifier.fillMaxWidth(),singleLine=true);OutlinedTextField(birthday,{birthday=it},label={Text("Birthday · YYYY-MM-DD (optional)")},modifier=Modifier.fillMaxWidth(),singleLine=true)}
            OutlinedTextField(email,{email=it},label={Text("Email")},modifier=Modifier.fillMaxWidth(),singleLine=true)
            OutlinedTextField(password,{password=it},label={Text("Password")},visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth(),singleLine=true)
            Primary(if(signup)"Create account" else "Sign in",email.isNotBlank()&&password.isNotBlank()){
                if(signup)vm.action{vm.repository.register(name,email,phone,password,birthday);vm.preferences.session(true)} else vm.login(email,password)
            }
            TextButton({signup=!signup}){Text(if(signup)"Already a member? Sign in" else "New here? Create an account")}
        }
        Primary("Explore demo account"){vm.demoLogin()}
        Muted("OFFLINE DEMO · Fictional data only.\nDemo: alex@example.test / hakumdemo\nThe demo button restores Alex if another local account is active.")
    }
}
