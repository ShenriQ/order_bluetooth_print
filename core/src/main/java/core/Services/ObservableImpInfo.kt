package core.Services

import core.Models.ImpInfo
import java.util.*

class ObservableImpInfo : Observable() {

    companion object {
        private val instance: ObservableImpInfo = ObservableImpInfo()
        fun getInstance(): ObservableImpInfo {
            return instance
        }
    }

    public fun change(impInfo: ImpInfo) {
        setChanged()
        notifyObservers(impInfo)
    }
}