package com.example.a01_compose_study.domain


import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.example.a01_compose_study.domain.util.CustomLogger


class MyLifecycleOwner : SavedStateRegistryOwner {
    private var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private var mSavedStateRegistryController: SavedStateRegistryController =
        SavedStateRegistryController.create(this)
    val TAG = this.javaClass.simpleName

    init {
        CustomLogger.i("$TAG Constructor Hash[${this.hashCode()}]")
    }

    val isInitialized: Boolean
        get() = true
    override val lifecycle: Lifecycle
        get() = mLifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = mSavedStateRegistryController.savedStateRegistry

    fun setCurrentState(state: Lifecycle.State) {
        mLifecycleRegistry.currentState = state
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        CustomLogger.i("handleLifecycleEvent")
        mLifecycleRegistry.handleLifecycleEvent(event)
    }

    fun performRestore(savedState: Bundle?) {
        CustomLogger.i("performRestore")
        try {
            mSavedStateRegistryController.performRestore(savedState)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun performSave(outBundle: Bundle) {
        mSavedStateRegistryController.performSave(outBundle)
    }
}