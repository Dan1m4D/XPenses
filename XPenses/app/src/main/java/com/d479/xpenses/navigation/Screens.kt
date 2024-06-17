package com.d479.xpenses.navigation

sealed class Screens(val route: String) {
    object Home : Screens("home")
    object Expenses : Screens("expenses")
    object Map : Screens("map")
    object Split : Screens("split")
    object Analytics : Screens("analytics")
    object SignIn : Screens("signIn")
    object Scan: Screens("scan")
}