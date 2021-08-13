package core.Listeners;

/**
 * Created by Kamran Ahmed on 4/30/2015.
 */
public interface PaginationListener<T,E> {

    public void success(T success);
    public void error(E error);
    public void nextPage(boolean hasNextPage);
}
