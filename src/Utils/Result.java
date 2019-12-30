package Utils;

import java.util.Optional;
import java.util.function.Function;

public interface Result<V, E> {

    static <T, E> Result <T, E> Ok(T value) {
        return new Ok<>(value);
    }

    static <T, E> Result <T, E> Err(E err) {
        return new Err<>(err);
    }

    static <T, E> Result<T, E> of_nullable(T value, E err) {
        if(value == null)
            return new Err<>(err);
        return new Ok<>(value);
    }

    boolean is_ok();

    boolean is_err();

    Optional<V> ok();

    Optional<E> err();

    <R> Result<R, E> map(Function<? super V, ? extends R> f);

    Result<V, E> or(Result<V, E> r);

    Result<V,E> clone();

    <R> Result<V, R> map_err(Function<? super E, ? extends R> f);

    V unwrap();

    E unwrap_err();

    <R> Result<R, E> and_then(Function<? super V, ? extends Result<R, E>> f);
}
