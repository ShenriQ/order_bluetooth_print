package core.Services

import core.Models.User
import java.util.*

class ObservableOrder : Observable() {

    companion object {
        private val instance: ObservableOrder =
            ObservableOrder()
        fun getInstance(): ObservableOrder {
            return instance
        }
    }

    public fun change(priceAndCount: Pair<Double,Int>) {
        setChanged()
        notifyObservers(priceAndCount)
    }
}