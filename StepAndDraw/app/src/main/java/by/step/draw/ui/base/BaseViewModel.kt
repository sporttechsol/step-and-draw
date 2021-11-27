package by.step.draw.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel : ViewModel() {

    private val disposables: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    public override fun onCleared() {
        disposables.dispose()
    }

    protected fun removeDisposable(disposable: Disposable?) {
        disposable?.let {
            disposables.remove(it)
        }
    }

    protected fun addDisposable(disposable: Disposable?) {
        disposable?.let {
            disposables.add(it)
        }
    }
}