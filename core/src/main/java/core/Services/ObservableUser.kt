package core.Services

import core.Models.User
import java.util.*

class ObservableUser : Observable() {

    companion object {
        private val instance: ObservableUser = ObservableUser()
        fun getInstance(): ObservableUser {
            return instance
        }
    }

    public fun change(user: User) {
        setChanged()
        notifyObservers(user)
    }
}