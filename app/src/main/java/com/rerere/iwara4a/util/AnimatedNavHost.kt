package com.rerere.iwara4a.util

/*
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.DialogHost
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.LocalOwnersProvider
import androidx.navigation.compose.NamedNavArgument
import com.rerere.iwara4a.util.AnimatedComposeNavigator.Destination
import kotlin.collections.set

@ExperimentalAnimationApi
@Composable
fun AnimatedNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    route: String? = null,
    enterTransition: (initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition =
        { _, _ -> fadeIn(animationSpec = tween(2000)) },
    exitTransition: (initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition =
        { _, _ -> fadeOut(animationSpec = tween(2000)) },
    builder: NavGraphBuilder.() -> Unit
) {
    val graph = remember(route, startDestination, builder) {
        navController.createGraph(startDestination, route, builder)
    }.apply {
        enterTransitions[route] = enterTransition
        exitTransitions[route] = exitTransition
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "NavHost requires a ViewModelStoreOwner to be provided via LocalViewModelStoreOwner"
    }
    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val onBackPressedDispatcher = onBackPressedDispatcherOwner?.onBackPressedDispatcher

    // on successful recompose we setup the navController with proper inputs
    // after the first time, this will only happen again if one of the inputs changes
    navController.setLifecycleOwner(lifecycleOwner)
    navController.setViewModelStore(viewModelStoreOwner.viewModelStore)
    if (onBackPressedDispatcher != null) {
        navController.setOnBackPressedDispatcher(onBackPressedDispatcher)
    }

    navController.graph = graph

    val saveableStateHolder = rememberSaveableStateHolder()

    // Find the ComposeNavigator, returning early if it isn't found
    // (such as is the case when using TestNavHostController)
    val composeNavigator = navController.navigatorProvider.get<Navigator<out NavDestination>>(
        AnimatedComposeNavigator.NAME
    ) as? AnimatedComposeNavigator ?: return
    val backStack by composeNavigator.backStack.collectAsState()
    val transitionsInProgress by composeNavigator.transitionsInProgress.collectAsState()

    val backStackEntry = transitionsInProgress.keys.lastOrNull { entry ->
        entry.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    } ?: backStack.lastOrNull { entry ->
        entry.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

    if (backStackEntry != null) {
        val destination = backStackEntry.destination as Destination

        val leavingEntry = transitionsInProgress.keys.lastOrNull { entry ->
            !entry.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        }

        // When there is no leaving entry, that means this is the start destination so this
        // transition never happens.
        val finalEnter = if (leavingEntry != null) {
            destination.enterTransition?.invoke(leavingEntry, backStackEntry)
                ?: enterTransitions[
                        (destination.hierarchy.first { enterTransitions.containsKey(it.route) }).route
                ]?.invoke(leavingEntry, backStackEntry) as EnterTransition
        } else {
            EnterTransition.None
        }

        val finalExit = if (leavingEntry != null) {
            (leavingEntry.destination as? Destination)?.exitTransition?.invoke(
                leavingEntry, backStackEntry
            ) ?: exitTransitions[
                    (
                            leavingEntry.destination.hierarchy.first {
                                exitTransitions.containsKey(it.route)
                            }
                            ).route
            ]?.invoke(leavingEntry, backStackEntry) as ExitTransition
        } else {
            ExitTransition.None
        }
        val transition = updateTransition(backStackEntry, label = "entry")
        transition.AnimatedContent(
            modifier, transitionSpec = { finalEnter with finalExit }
        ) { currentEntry ->
            // while in the scope of the composable, we provide the navBackStackEntry as the
            // ViewModelStoreOwner and LifecycleOwner
            currentEntry.LocalOwnersProvider(saveableStateHolder) {
                (currentEntry.destination as Destination).content(currentEntry)
            }
        }
        if (transition.currentState == transition.targetState) {
            transitionsInProgress.forEach { entry ->
                entry.value.onTransitionComplete()
            }
        }
    }

    val dialogNavigator = navController.navigatorProvider.get<Navigator<out NavDestination>>(
        "dialog"
    ) as? DialogNavigator ?: return

    // Show any dialog destinations
    DialogHost(dialogNavigator)
}

*/
/**
 * Add the [Composable] to the [NavGraphBuilder]
 *
 * @param route route for the destination
 * @param arguments list of arguments to associate with destination
 * @param deepLinks list of deep links to associate with the destinations
 * @param content composable for the destination
 *//*

@ExperimentalAnimationApi
fun NavGraphBuilder.composable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (
        (initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition?
    )? = null,
    exitTransition: (
        (initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition?
    )? = null,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    addDestination(
        Destination(
            provider[AnimatedComposeNavigator::class],
            content,
            enterTransition,
            exitTransition
        ).apply {
            this.route = route
            arguments.forEach { (argumentName, argument) ->
                addArgument(argumentName, argument)
            }
            deepLinks.forEach { deepLink ->
                addDeepLink(deepLink)
            }
        }
    )
}

@ExperimentalAnimationApi
fun NavGraphBuilder.navigation(
    startDestination: String,
    route: String,
    enterTransition: ((initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition)? =
        { _, _ -> fadeIn() },
    exitTransition: ((initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition)? =
        { _, _ -> fadeOut() },
    builder: NavGraphBuilder.() -> Unit
) {
    navigation(startDestination, route, builder).apply {
        enterTransition?.let { enterTransitions[route] = enterTransition }
        exitTransition?.let { exitTransitions[route] = exitTransition }
    }
}

@ExperimentalAnimationApi
internal val enterTransitions =
    mutableMapOf<String?,
                (initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition>()

@ExperimentalAnimationApi
internal val exitTransitions =
    mutableMapOf<String?,
                (initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition>()

*/
/**
 * Navigator that navigates through [Composable]s. Every destination using this Navigator must
 * set a valid [Composable] by setting it directly on an instantiated [Destination] or calling
 * [composable].
 *//*

@ExperimentalAnimationApi
@Navigator.Name("animatedComposable")
class AnimatedComposeNavigator : Navigator<Destination>() {
    internal val transitionsInProgress get() = state.transitionsInProgress

    internal val backStack get() = state.backStack

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) {
        entries.forEach { entry ->
            state.pushWithTransition(entry)
        }
    }

    override fun createDestination(): Destination {
        return Destination(this, content = { })
    }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        state.popWithTransition(popUpTo, savedState)
    }

    */
/**
     * NavDestination specific to [AnimatedComposeNavigator]
     *//*

    @ExperimentalAnimationApi
    @NavDestination.ClassType(Composable::class)
    class Destination(
        navigator: AnimatedComposeNavigator,
        internal val content: @Composable (NavBackStackEntry) -> Unit,
        internal var enterTransition:
        ((initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition?)? = null,
        internal var exitTransition:
        ((initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition?)? = null
    ) : NavDestination(navigator)

    internal companion object {
        internal const val NAME = "animatedComposable"
    }
}
*/
