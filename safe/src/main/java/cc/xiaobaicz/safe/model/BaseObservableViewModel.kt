package cc.xiaobaicz.safe.model

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel

abstract class BaseObservableViewModel : ViewModel(), Observable {

    @Transient
    private val registry = PropertyChangeRegistry()

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        registry.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        registry.remove(callback)
    }

    open fun notifyChange() {
        registry.notifyCallbacks(this, 0, null)
    }

    open fun notifyPropertyChanged(fieldId: Int) {
        registry.notifyCallbacks(this, fieldId, null)
    }

}