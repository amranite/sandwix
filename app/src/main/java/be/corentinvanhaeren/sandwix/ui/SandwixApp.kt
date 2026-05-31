package be.corentinvanhaeren.sandwix.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import be.corentinvanhaeren.sandwix.R
import be.corentinvanhaeren.sandwix.model.CartItem
import be.corentinvanhaeren.sandwix.model.CustomerOrder
import be.corentinvanhaeren.sandwix.model.sampleOrders
import be.corentinvanhaeren.sandwix.network.SandwixApi
import be.corentinvanhaeren.sandwix.ui.components.SandwixBottomBar
import be.corentinvanhaeren.sandwix.ui.components.SandwixTopBar
import be.corentinvanhaeren.sandwix.ui.navigation.MainTab
import be.corentinvanhaeren.sandwix.ui.navigation.Route
import be.corentinvanhaeren.sandwix.ui.screens.cart.CartScreen
import be.corentinvanhaeren.sandwix.ui.screens.checkout.CheckoutScreen
import be.corentinvanhaeren.sandwix.ui.screens.checkout.CheckoutViewModel
import be.corentinvanhaeren.sandwix.ui.screens.confirmation.ConfirmationScreen
import be.corentinvanhaeren.sandwix.ui.screens.detail.SandwichDetailScreen
import be.corentinvanhaeren.sandwix.ui.screens.detail.SandwichDetailViewModel
import be.corentinvanhaeren.sandwix.ui.screens.home.HomeScreen
import be.corentinvanhaeren.sandwix.ui.screens.home.HomeViewModel
import be.corentinvanhaeren.sandwix.ui.screens.login.LoginScreen
import be.corentinvanhaeren.sandwix.ui.screens.login.LoginViewModel
import be.corentinvanhaeren.sandwix.ui.screens.orders.OrderDetailScreen
import be.corentinvanhaeren.sandwix.ui.screens.orders.OrdersScreen
import be.corentinvanhaeren.sandwix.ui.screens.profile.ProfileScreen
import be.corentinvanhaeren.sandwix.ui.screens.register.RegisterScreen
import be.corentinvanhaeren.sandwix.ui.screens.register.RegisterViewModel
import be.corentinvanhaeren.sandwix.ui.screens.employee.EmployeeOrderDetailScreen
import be.corentinvanhaeren.sandwix.ui.screens.employee.orders.EmployeeOrdersScreen
import be.corentinvanhaeren.sandwix.ui.screens.employee.EmployeeProfileScreen
import be.corentinvanhaeren.sandwix.ui.screens.employee.EmployeeScanScreen
import be.corentinvanhaeren.sandwix.ui.theme.SandwixTheme
import be.corentinvanhaeren.sandwix.data.SandwixRepository
import be.corentinvanhaeren.sandwix.data.TokenStore
import be.corentinvanhaeren.sandwix.network.SandwixApiService
import be.corentinvanhaeren.sandwix.ui.navigation.EmployeeTab
import be.corentinvanhaeren.sandwix.ui.components.SandwixEmployeeBottomBar
import be.corentinvanhaeren.sandwix.ui.screens.employee.orders.EmployeeOrdersViewModel
import be.corentinvanhaeren.sandwix.ui.screens.employee.orderdetails.EmployeeOrderDetailViewModel
@Composable
fun SandwixApp() {
    val context = LocalContext.current.applicationContext
    val tokenStore = remember { TokenStore(context) }
    val apiService = remember { SandwixApi.create(context) }
    val repository = remember { SandwixRepository(apiService) }

    val navController = rememberNavController()

    var token by rememberSaveable { mutableStateOf(tokenStore.getToken()) }
    var gebruikerId by rememberSaveable { mutableStateOf(tokenStore.getGebruikerId()) }
    var rol by rememberSaveable { mutableStateOf(tokenStore.getRol()) }

    fun isEmployeeRole(value: String?): Boolean {
        return value.equals("medewerker", ignoreCase = true) ||
                value.equals("employee", ignoreCase = true)
    }

    val startDestination = when {
        !tokenStore.isLoggedIn() -> Route.Login.routeName
        isEmployeeRole(rol) -> Route.EmployeeOrders.routeName
        else -> Route.Home.routeName
    }

    val cart = remember { mutableStateListOf<CartItem>() }

    val orders = remember {
        mutableStateListOf<CustomerOrder>().apply {
            addAll(sampleOrders())
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Route.Login.routeName

    val selectedCustomerTab = MainTab.entries.firstOrNull { tab ->
        tab.route.routeName == currentRoute ||
                tab.route == Route.Orders && currentRoute == Route.OrderDetail.routeName
    }

    val selectedEmployeeTab = EmployeeTab.entries.firstOrNull { tab ->
        tab.route.routeName == currentRoute ||
                tab.route == Route.EmployeeOrders && currentRoute == Route.EmployeeOrderDetail.routeName
    }

    val title = when (currentRoute) {
        Route.Login.routeName -> stringResource(Route.Login.titleRes)
        Route.Register.routeName -> stringResource(Route.Register.titleRes)

        Route.Home.routeName -> stringResource(Route.Home.titleRes)
        Route.Detail.routeName -> stringResource(Route.Detail.titleRes)
        Route.Cart.routeName -> stringResource(Route.Cart.titleRes)
        Route.Checkout.routeName -> stringResource(Route.Checkout.titleRes)
        Route.Confirmation.routeName -> stringResource(Route.Confirmation.titleRes)
        Route.Orders.routeName -> stringResource(Route.Orders.titleRes)

        Route.OrderDetail.routeName -> {
            val orderId = backStackEntry?.arguments?.getInt(Route.ORDER_ID_ARG)

            if (orderId != null) {
                stringResource(R.string.order_detail_title, orderId)
            } else {
                stringResource(R.string.nav_orders)
            }
        }

        Route.Profile.routeName -> stringResource(Route.Profile.titleRes)

        Route.EmployeeOrders.routeName -> stringResource(Route.EmployeeOrders.titleRes)
        Route.EmployeeScan.routeName -> stringResource(Route.EmployeeScan.titleRes)
        Route.EmployeeProfile.routeName -> stringResource(Route.EmployeeProfile.titleRes)

        Route.EmployeeOrderDetail.routeName -> {
            val orderId = backStackEntry?.arguments?.getInt(Route.ORDER_ID_ARG)

            if (orderId != null) {
                stringResource(R.string.order_detail_title, orderId)
            } else {
                stringResource(Route.EmployeeOrderDetail.titleRes)
            }
        }

        else -> stringResource(R.string.app_name_display)
    }

    val canNavigateBack = currentRoute in listOf(
        Route.Register.routeName,
        Route.Detail.routeName,
        Route.Checkout.routeName,
        Route.OrderDetail.routeName,
        Route.EmployeeOrderDetail.routeName,
    )

    fun navigateToCustomerTab(route: Route) {
        navController.navigate(route.routeName) {
            popUpTo(Route.Home.routeName) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToEmployeeTab(route: Route) {
        navController.navigate(route.routeName) {
            popUpTo(Route.EmployeeOrders.routeName) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateAfterLoginOrRegister(loginRol: String) {
        val destination = if (isEmployeeRole(loginRol)) {
            Route.EmployeeOrders
        } else {
            Route.Home
        }

        navController.navigate(destination.routeName) {
            popUpTo(Route.Login.routeName) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun logout() {
        val startRoute = if (isEmployeeRole(rol)) {
            Route.EmployeeOrders.routeName
        } else {
            Route.Home.routeName
        }

        tokenStore.clearSession()

        token = null
        gebruikerId = null
        rol = null

        navController.navigate(Route.Login.routeName) {
            popUpTo(startRoute) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    Scaffold(
        topBar = {
            SandwixTopBar(
                title = title,
                onBack = if (canNavigateBack) {
                    { navController.navigateUp() }
                } else {
                    null
                },
            )
        },
        bottomBar = {
            if (isEmployeeRole(rol)) {
                selectedEmployeeTab?.let { tab ->
                    SandwixEmployeeBottomBar(
                        selectedTab = tab,
                        onTabSelected = ::navigateToEmployeeTab,
                    )
                }
            } else {
                selectedCustomerTab?.let { tab ->
                    SandwixBottomBar(
                        selectedTab = tab,
                        onTabSelected = ::navigateToCustomerTab,
                    )
                }
            }
        },
    ) { innerPadding ->
        SandwixNavHost(
            navController = navController,
            contentPadding = innerPadding,
            cart = cart,
            orders = orders,
            onNavigateToCustomerTab = ::navigateToCustomerTab,
            onNavigateToEmployeeTab = ::navigateToEmployeeTab,
            onNavigateAfterLoginOrRegister = ::navigateAfterLoginOrRegister,
            onLogout = ::logout,
            gebruikerId = gebruikerId,
            rol = rol,
            onLoginSuccess = { responseToken, responseGebruikerId, responseRol ->
                tokenStore.saveSession(
                    token = responseToken,
                    gebruikerId = responseGebruikerId,
                    rol = responseRol,
                )

                token = responseToken
                gebruikerId = responseGebruikerId
                rol = responseRol
            },
            apiService = apiService,
            repository = repository,
            startDestination = startDestination,
        )
    }
}

@Composable
private fun SandwixNavHost(
    navController: NavHostController,
    contentPadding: PaddingValues,
    cart: MutableList<CartItem>,
    orders: MutableList<CustomerOrder>,
    onNavigateToCustomerTab: (Route) -> Unit,
    onNavigateToEmployeeTab: (Route) -> Unit,
    onNavigateAfterLoginOrRegister: (String) -> Unit,
    onLogout: () -> Unit,
    gebruikerId: Int?,
    rol: String?,
    onLoginSuccess: (String, Int, String) -> Unit,
    apiService: SandwixApiService,
    repository: SandwixRepository,
    startDestination: String,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    )  {
        composable(route = Route.Login.routeName) {
            val loginViewModel: LoginViewModel = viewModel(
                factory = ViewModelFactory {
                    LoginViewModel(
                        apiService = apiService
                    )
                }
            )

            val loginUiState by loginViewModel.uiState.collectAsState()

            LoginScreen(
                contentPadding = contentPadding,
                authUiState = loginUiState,
                onEmailUpdate = loginViewModel::onEmailUpdate,
                onPasswordUpdate = loginViewModel::onPasswordUpdate,
                onLogin = {
                    loginViewModel.login { responseToken, responseGebruikerId, responseRol ->
                        onLoginSuccess(
                            responseToken,
                            responseGebruikerId,
                            responseRol
                        )

                        onNavigateAfterLoginOrRegister(responseRol)
                    }
                },
                onRegister = {
                    navController.navigate(Route.Register.routeName)
                },
            )
        }

        composable(route = Route.Register.routeName) {
            val registerViewModel: RegisterViewModel = viewModel(
                factory = ViewModelFactory {
                    RegisterViewModel(
                        apiService = apiService
                    )
                }
            )
            val registerUiState by registerViewModel.uiState.collectAsState()

            RegisterScreen(
                contentPadding = contentPadding,
                registerUiState = registerUiState,
                onNaamUpdate = registerViewModel::onNaamUpdate,
                onEmailUpdate = registerViewModel::onEmailUpdate,
                onTelefoonnummerUpdate = registerViewModel::onTelefoonnummerUpdate,
                onWachtwoordUpdate = registerViewModel::onWachtwoordUpdate,
                onBevestigWachtwoordUpdate = registerViewModel::onBevestigWachtwoordUpdate,
                onRegister = {
                    registerViewModel.register(
                        onRegisterSuccess = {
                            navController.navigate(Route.Login.routeName) {
                                popUpTo(Route.Register.routeName) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                },
                onLogin = {
                    navController.navigateUp()
                },
            )
        }

        composable(route = Route.Home.routeName) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = ViewModelFactory {
                    HomeViewModel(
                        repository = repository
                    )
                }
            )
            val homeUiState by homeViewModel.uiState.collectAsState()


            /*
            LaunchedEffect(Unit) {
                homeViewModel.getBroodjes()
            }
             */
            HomeScreen(
                contentPadding = contentPadding,
                homeUiState = homeUiState,
                onQueryUpdate = homeViewModel::onQueryUpdate,
                onRetry = homeViewModel::getBroodjes,
                onSandwichClick = { sandwich ->
                    navController.navigate(Route.detailRoute(sandwich.id))
                },
            )
        }

        composable(
            route = Route.Detail.routeName,
            arguments = listOf(
                navArgument(Route.SANDWICH_ID_ARG) {
                    type = NavType.IntType
                },
            ),
        ) { backStackEntry ->
            val sandwichId = backStackEntry.arguments
                ?.getInt(Route.SANDWICH_ID_ARG)
                ?: return@composable

            val detailViewModel: SandwichDetailViewModel = viewModel(
                key = "sandwich-detail-$sandwichId",
                factory = ViewModelFactory {
                    SandwichDetailViewModel(
                        sandwichId = sandwichId,
                        repository = repository,
                    )
                }
            )
            val detailUiState by detailViewModel.uiState.collectAsState()

            SandwichDetailScreen(
                contentPadding = contentPadding,
                detailUiState = detailUiState,
                onRetry = detailViewModel::loadSandwich,
                onAddToCart = { item ->
                    val existingIndex = cart.indexOfFirst { existingItem ->
                        existingItem.sandwich.id == item.sandwich.id &&
                                existingItem.selectedExtras == item.selectedExtras &&
                                existingItem.note == item.note
                    }

                    if (existingIndex == -1) {
                        cart.add(item)
                    } else {
                        val existingItem = cart[existingIndex]
                        cart[existingIndex] = existingItem.copy(
                            quantity = existingItem.quantity + item.quantity
                        )
                    }
                    onNavigateToCustomerTab(Route.Cart)
                },
            )
        }

        composable(route = Route.Cart.routeName) {
            CartScreen(
                contentPadding = contentPadding,
                cartItems = cart,
                onContinueShopping = {
                    onNavigateToCustomerTab(Route.Home)
                },
                onCheckout = {
                    navController.navigate(Route.Checkout.routeName)
                },
                onQuantityChange = { item, quantity ->
                    val index = cart.indexOf(item)

                    if (index != -1) {
                        if (quantity <= 0) {
                            cart.removeAt(index)
                        } else {
                            cart[index] = item.copy(quantity = quantity)
                        }
                    }
                },
                onRemove = { item ->
                    cart.remove(item)
                },
            )
        }

        composable(route = Route.Checkout.routeName) {
            val currentGebruikerId = gebruikerId ?: return@composable
            val checkoutViewModel: CheckoutViewModel = viewModel(
                factory = ViewModelFactory {
                    CheckoutViewModel(
                        gebruikerId = currentGebruikerId,
                        apiService = apiService,
                    )
                }
            )
            val checkoutUiState by checkoutViewModel.uiState.collectAsState()

            CheckoutScreen(
                contentPadding = contentPadding,
                cartItems = cart,
                uiState = checkoutUiState,
                onRetry = checkoutViewModel::loadLocations,
                onLocationSelected = checkoutViewModel::selectLocation,
                onDateSelected = checkoutViewModel::selectDate,
                onTimeSelected = checkoutViewModel::selectTime,
                onNoteUpdate = checkoutViewModel::updateNote,
                onConfirm = {
                    checkoutViewModel.submitOrder(cart.toList()) { order ->
                    orders.add(0, order)
                    cart.clear()

                    navController.navigate(Route.confirmationRoute(order.id))
                    }
                },
            )
        }

        composable(
            route = Route.Confirmation.routeName,
            arguments = listOf(
                navArgument(Route.ORDER_ID_ARG) {
                    type = NavType.IntType
                },
            ),
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments
                ?.getInt(Route.ORDER_ID_ARG)
                ?: return@composable

            val order = orders.firstOrNull { order ->
                order.id == orderId
            } ?: return@composable

            ConfirmationScreen(
                contentPadding = contentPadding,
                order = order,
                onHome = {
                    onNavigateToCustomerTab(Route.Home)
                },
                onOrders = {
                    onNavigateToCustomerTab(Route.Orders)
                },
            )
        }

        composable(route = Route.Orders.routeName) {
            OrdersScreen(
                contentPadding = contentPadding,
                orders = orders,
                onOrderClick = { order ->
                    navController.navigate(Route.orderDetailRoute(order.id))
                },
                onHome = {
                    onNavigateToCustomerTab(Route.Home)
                },
            )
        }

        composable(
            route = Route.OrderDetail.routeName,
            arguments = listOf(
                navArgument(Route.ORDER_ID_ARG) {
                    type = NavType.IntType
                },
            ),
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments
                ?.getInt(Route.ORDER_ID_ARG)
                ?: return@composable

            val order = orders.firstOrNull { order ->
                order.id == orderId
            } ?: return@composable

            OrderDetailScreen(
                contentPadding = contentPadding,
                order = order,
                onHome = {
                    onNavigateToCustomerTab(Route.Home)
                },
            )
        }

        composable(route = Route.Profile.routeName) {
            ProfileScreen(
                contentPadding = contentPadding,
                onLogout = onLogout,
            )
        }

        composable(route = Route.EmployeeOrders.routeName) {
            val employeeOrdersViewModel: EmployeeOrdersViewModel = viewModel(
                factory = ViewModelFactory {
                    EmployeeOrdersViewModel(
                        apiService = apiService,
                    )
                }
            )

            val employeeOrdersUiState by employeeOrdersViewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                employeeOrdersViewModel.getBestellingenVandaag()
            }

            EmployeeOrdersScreen(
                contentPadding = contentPadding,
                uiState = employeeOrdersUiState,
                onRetry = employeeOrdersViewModel::getBestellingenVandaag,
                onOrderClick = { order ->
                    navController.navigate(
                        Route.employeeOrderDetailRoute(order.bestellingId)
                    )
                },
            )
        }

        composable(
            route = Route.EmployeeOrderDetail.routeName,
            arguments = listOf(
                navArgument(Route.ORDER_ID_ARG) {
                    type = NavType.IntType
                },
            ),
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments
                ?.getInt(Route.ORDER_ID_ARG)
                ?: return@composable

            val employeeOrderDetailViewModel: EmployeeOrderDetailViewModel = viewModel(
                key = "employee-order-detail-$orderId",
                factory = ViewModelFactory {
                    EmployeeOrderDetailViewModel(
                        bestellingId = orderId,
                        apiService = apiService,
                    )
                }
            )

            val employeeOrderDetailUiState by employeeOrderDetailViewModel.uiState.collectAsState()

            EmployeeOrderDetailScreen(
                contentPadding = contentPadding,
                uiState = employeeOrderDetailUiState,
                onRetry = employeeOrderDetailViewModel::loadOrder,
                onStatusSave = employeeOrderDetailViewModel::updateStatus,
            )
        }
        composable(route = Route.EmployeeScan.routeName) {
            EmployeeScanScreen(
                contentPadding = contentPadding,
                onStartScan = { /* TODO: Implement scan */ },
                onSearchOrder = { code ->
                    val order = orders.firstOrNull { it.pickupCode == code }
                    if (order != null) {
                        navController.navigate(Route.employeeOrderDetailRoute(order.id))
                    }
                },
            )
        }

        composable(route = Route.EmployeeProfile.routeName) {
            EmployeeProfileScreen(
                contentPadding = contentPadding,
                name = "Medewerker",
                email = "medewerker@sandwix.be",
                onPersonalDataClick = { },
                onLogout = onLogout,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SandwixAppPreview() {
    SandwixTheme {
        SandwixApp()
    }
}
