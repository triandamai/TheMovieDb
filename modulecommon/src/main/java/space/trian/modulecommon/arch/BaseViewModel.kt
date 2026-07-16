package space.trian.modulecommon.arch

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass


@Stable
abstract class BaseViewModel<State, Action : Any, Event>(state: State) : ViewModel() {


    private val effects: HashMap<String, suspend (Event) -> Unit> = HashMap()
    private val _uiState: MutableStateFlow<State> = MutableStateFlow(state)
    val uiState = _uiState.asStateFlow()


    private val action: HashMap<String, suspend (Action) -> Unit> = HashMap()

    init {
        this.onAction()
    }
    abstract fun doInit()
    abstract fun doRefreshInit()
    abstract fun onAction()


    fun removeEffectListener() {
        effects.remove("MAIN")
    }

    fun removeEffectListener(key: String) {
        effects.remove(key)
    }

    fun addOnEventListener(listener: suspend (Event) -> Unit) {
        effects["MAIN"] = listener
        doInit()
    }

    fun addOnEventListener(key: String, listener: suspend (Event) -> Unit) {
        effects[key] = listener
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Action> on(name: KClass<T>, cb: suspend (T) -> Unit) {
        name.simpleName?.let {
            action[it] = cb as suspend (Action) -> Unit
        }
    }

    fun updateState(cb: State.() -> State) = _uiState.update(cb)

    fun commit(cb: suspend State.() -> State) {
        viewModelScope.async { _uiState.tryEmit(cb(uiState.value)) }
    }


    fun invokeAction(act: Action) {
        viewModelScope.launch { action[act::class.simpleName.orEmpty()]?.invoke(act) }
    }



    private fun sendEvent(event: Event) = viewModelScope.launch {
        effects.forEach { (_, callback) -> callback(event) }
    }


    fun send(event: Event) {
        sendEvent(event)
    }




}
