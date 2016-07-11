package lesson.gamer.com.gl.one;

/**
 * Created by daiepngfei on 7/11/16
 */
public interface CCallback<T> {

    /**
     *
     * @param t
     */
    void callback(T t);

    /**
     *
     * @param e
     */
    void reject(Exception e);

}
