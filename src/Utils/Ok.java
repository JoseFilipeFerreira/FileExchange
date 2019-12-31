package Utils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class Ok<V, E> implements Result<V, E> {
    private V value;

    public Ok(V value) {
        this.value = value;
    }

    private Ok(Ok<V, E> a) {
        this.value = a.value;
    }

    public boolean is_ok() {
        return true;
    }

    public boolean is_err() {
        return false;
    }

    public Optional<V> ok() {
        return Optional.of(value);
    }

    public Optional<E> err() {
        return Optional.empty();
    }

    public <R> Result<R, E> map(Function<? super V, ? extends R> f) {
        return new Ok<>(f.apply(this.value));
    }

    public <R> Result<V, R> map_err(Function<? super E, ? extends R> f) {
        return new Ok<>(this.value);
    }


    public Result<V, E> or(Result<V, E> r) {
        return new Ok<>(this);
    }

    public Result<V, E> clone() {
        return new Ok<>(this);
    }

    public V unwrap() {
        return this.value;
    }

    public Result<V, E> also(Consumer<V> f) {
        f.accept(this.value);
        return this;
    }

    public E unwrap_err() {
        return null;
    }

    public <R> Result<R, E> and_then(Function<? super V, ? extends Result<R, E>> f) {
        return f.apply(this.value);
    }

    public String toString() {
        return "{status='ok', result='" + value + "'}";
    }
}
