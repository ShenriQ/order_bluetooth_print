package core.Listeners

/**
 * Created by Kamran Ahmed on 4/30/2015.
 */
interface ServiceListener<T, E> {
    fun success(success: T)
    fun error(error: E)
}