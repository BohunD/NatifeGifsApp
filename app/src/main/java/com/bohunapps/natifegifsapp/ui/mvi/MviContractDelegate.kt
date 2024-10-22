package com.bohunapps.natifegifsapp.ui.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MviContractDelegate<STATE, EVENT, EFFECT> internal constructor(
    initialUiState: STATE,
) : UnidirectionalViewModel<STATE, EVENT, EFFECT> {

    private val _state = MutableStateFlow(initialUiState)
    override val state: StateFlow<STATE>
        get() = _state.asStateFlow()

    private val _effect by lazy { Channel<EFFECT>() }
    override val effect: Flow<EFFECT> by lazy { _effect.receiveAsFlow() }

    override fun event(event: EVENT) {}

    override fun updateUiState(newUiState: STATE) {
        _state.update { newUiState }
    }

    override fun updateUiState(block: STATE.() -> STATE) {
        _state.update(block)
    }

    override fun CoroutineScope.emitSideEffect(effect: EFFECT) {
        this.launch { _effect.send(effect) }
    }
}

fun <STATE, EVENT, EFFECT> mvi(
    initialUiState: STATE,
): UnidirectionalViewModel<STATE, EVENT, EFFECT> = MviContractDelegate(initialUiState)

@Composable
inline fun <reified STATE, EVENT, EFFECT> use(
    viewModel: UnidirectionalViewModel<STATE, EVENT, EFFECT>,
): StateDispatchEffect<STATE, EVENT, EFFECT> {
    val state by viewModel.state.collectAsStateWithLifecycle(androidx.compose.ui.platform.LocalLifecycleOwner.current)

    val dispatch: (EVENT) -> Unit = remember {
        { event ->
            viewModel.event(event)
        }
    }

    return StateDispatchEffect(
        state = state,
        effectFlow = viewModel.effect,
        dispatch = dispatch,
    )
}
data class StateDispatchEffect<STATE, EVENT, EFFECT>(
    val state: STATE,
    val dispatch: (EVENT) -> Unit,
    val effectFlow: Flow<EFFECT>,
)