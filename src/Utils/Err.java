package Utils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class Err<V, E> implements Result<V, E> {

    private E err;

    public Err(E err) {
        this.err = err;
    }

    private Err(Err<V, E> a) {
        this.err = a.err;
    }

    public boolean is_ok() {
        return false;
    }

    public boolean is_err() {
        return true;
    }

    public Optional<V> ok() {
        return Optional.empty();
    }

    public Optional<E> err() {
        return Optional.of(this.err);
    }

    public <R> Result<R, E> map(Function<? super V, ? extends R> f) {
        return new Err<>(this.err);
    }

    public <R> Result<V, R> map_err(Function<? super E, ? extends R> f) {
        return new Err<>(f.apply(this.err));
    }

    public Result<V, E> or(Result<V, E> r) {
        return r.clone();
    }

    public Result<V, E> clone() {
        return new Err<>(this);
    }

    public V unwrap() {
        return null;
    }

    public E unwrap_err() {
        return this.err;
    }


    public Result<V, E> also(Consumer<V> f) {
        return this;
    }

    public <R> Result<R, E> and_then(Function<? super V, ? extends Result<R, E>> f) {
        return new Err<R, E>(this.err);
    }

    public String toString() {
        return "{status='failed', result='" + err + "'}";
    }
}
