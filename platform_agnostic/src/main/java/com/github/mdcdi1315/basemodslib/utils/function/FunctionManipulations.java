package com.github.mdcdi1315.basemodslib.utils.function;

import com.github.mdcdi1315.DotNetLayer.System.*;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import java.util.function.Function;

/**
 * Provides static helper methods for manipulating functional interface objects in many different ways - and as such are useful.
 * @since 1.0.20
 */
public final class FunctionManipulations
{
    // Do not let anyone be able to instantiate this class.
    private FunctionManipulations() {}

    /**
     * Returns an aggregated predicate instance that does perform an AND operation on the two provided predicates.
     * @param predicate_1 The first predicate.
     * @param predicate_2 The second predicate.
     * @return A new {@link Predicate} instance providing the combined result of {@code predicate_1} and {@code predicate_2} arguments.
     * @param <T> The type of the elements that are to be tested against.
     * @throws ArgumentNullException {@code predicate_1} and/or {@code predicate_2} are {@code null}.
     */
    @NotNull
    public static <T> Predicate<T> Combine(Predicate<T> predicate_1, Predicate<T> predicate_2)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate_1, "predicate_1");
        ArgumentNullException.ThrowIfNull(predicate_2, "predicate_2");
        return ConstructAndPredicate(predicate_1, predicate_2);
    }

    /**
     * Returns an aggregated predicate instance that does perform an AND operation on the two provided predicates. <br />
     * This method variant specially handles the input arguments when either of them are {@code null}. <br />
     * If at least one of the input parameters is {@code null}, the {@code null} parameter is treated as &quot;implicitly {@code false}&quot; and an always false predicate is returned. <br />
     * Otherwise, the aggregated predicate is created.
     * @param predicate_1 The first predicate.
     * @param predicate_2 The second predicate.
     * @return A new {@link Predicate} instance providing the combined result of {@code predicate_1} and {@code predicate_2} arguments.
     * @param <T> The type of the elements that are to be tested against.
     */
    @NotNull
    public static <T> Predicate<T> NullableCombine(@AllowNull Predicate<T> predicate_1, @AllowNull Predicate<T> predicate_2)
    {
        return (predicate_1 == null || predicate_2 == null) ?
                // Either of the predicates is null, give always false.
                new AlwaysFalsePredicate<>() :
                // We are OK, we can construct the predicate.
                ConstructAndPredicate(predicate_1, predicate_2);
    }

    @NotNull
    private static <T> Predicate<T> ConstructAndPredicate(
            @DisallowNull Predicate<T> p1,
            @DisallowNull Predicate<T> p2
    ) {
        if (p1 instanceof AlwaysFalsePredicate<T> || p2 instanceof AlwaysFalsePredicate<T>) {
            return new AlwaysFalsePredicate<>();
        } else if (p1 instanceof AlwaysTruePredicate<T>) {
            return p2;
        } else if (p2 instanceof AlwaysTruePredicate<T>) {
            return p1;
        } else {
            return new AndPredicateFromTwo<>(p1, p2);
        }
    }

    /**
     * Returns an aggregated predicate instance that does perform an OR operation on the two provided predicates.
     * @param predicate_1 The first predicate.
     * @param predicate_2 The second predicate.
     * @return A new {@link Predicate} instance providing the either result of {@code predicate_1} and {@code predicate_2} arguments.
     * @param <T> The type of the elements that are to be tested against.
     * @throws ArgumentNullException {@code predicate_1} and/or {@code predicate_2} are {@code null}.
     */
    @NotNull
    public static <T> Predicate<T> Either(Predicate<T> predicate_1, Predicate<T> predicate_2)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate_1, "predicate_1");
        ArgumentNullException.ThrowIfNull(predicate_2, "predicate_2");
        return ConstructOrPredicate(predicate_1, predicate_2);
    }

    /**
     * Returns an aggregated predicate instance that does perform an AND operation on the two provided predicates. <br />
     * This method variant specially handles the input arguments when either of them are {@code null}. <br />
     * If at least one of the input parameters is {@code null}, the {@code null} parameter is treated as &quot;implicitly {@code false}&quot;. <br />
     * The below 4 bullets describe how this method treats input parameters:
     * <li>If {@code predicate_1} is {@code null}, and {@code predicate_2} is {@code null} as well, an always-false predicate is returned.</li>
     * <li>If {@code predicate_1} is {@code null}, and {@code predicate_2} is not {@code null}, the value of the {@code predicate_2} parameter is returned.</li>
     * <li>If {@code predicate_1} is not {@code null}, and {@code predicate_2} is {@code null}, the value of the {@code predicate_1} parameter is returned.</li>
     * <li>Otherwise, the aggregated predicate is created.</li>
     * @param predicate_1 The first predicate.
     * @param predicate_2 The second predicate.
     * @return A new {@link Predicate} instance providing the combined result of {@code predicate_1} and {@code predicate_2} arguments.
     * @param <T> The type of the elements that are to be tested against.
     */
    @NotNull
    public static <T> Predicate<T> NullableEither(@AllowNull Predicate<T> predicate_1, @AllowNull Predicate<T> predicate_2)
    {
        boolean p1null = predicate_1 == null, p2null = predicate_2 == null;
        if (p1null) {
            return p2null ? new AlwaysFalsePredicate<>() : predicate_2;
        } else if (p2null) {
            // We already asserted that predicate_1 is not null above.
            return predicate_1;
        } else {
            // We are OK, we can construct the predicate.
            return ConstructOrPredicate(predicate_1, predicate_2);
        }
    }

    @NotNull
    private static <T> Predicate<T> ConstructOrPredicate(
            @DisallowNull Predicate<T> p1,
            @DisallowNull Predicate<T> p2
    ) {
        if (p1 instanceof AlwaysFalsePredicate<T> && p2 instanceof AlwaysFalsePredicate<T>) {
            return new AlwaysFalsePredicate<>();
        } else if (p1 instanceof AlwaysTruePredicate<T> || p2 instanceof AlwaysTruePredicate<T>) {
            return new AlwaysTruePredicate<>();
        } else {
            return new OrPredicateFromTwo<>(p1, p2);
        }
    }

    /**
     * Returns an aggregated predicate instance that does perform an XOR operation on the two provided predicates.
     * @param predicate_1 The first predicate.
     * @param predicate_2 The second predicate.
     * @return A new {@link Predicate} instance providing the either result of {@code predicate_1} and {@code predicate_2} arguments.
     * @param <T> The type of the elements that are to be tested against.
     * @throws ArgumentNullException {@code predicate_1} and/or {@code predicate_2} are {@code null}.
     */
    @NotNull
    public static <T> Predicate<T> Xor(Predicate<T> predicate_1, Predicate<T> predicate_2)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(predicate_1, "predicate_1");
        ArgumentNullException.ThrowIfNull(predicate_2, "predicate_2");
        return new XorPredicateFromTwo<>(predicate_1, predicate_2);
    }

    /**
     * Returns an aggregated predicate instance that does negate the specified {@code predicate}.
     * @param predicate The predicate to negate.
     * @return A new {@link Predicate} representing the negated result of {@code predicate}.
     * @param <T> The type of the elements that are to be tested against.
     * @throws ArgumentNullException {@code predicate} is {@code null}.
     * @implNote For the sake of uniformity, negating an always-true or always-false predicate
     * returned through the {@link #AlwaysTrue()} and {@link #AlwaysFalse()} methods will yield
     * returning the negations of the results of these methods. <br />
     * (That is, an instance returned through the {@link #AlwaysFalse()} method will be returned
     * when the input predicate is always-true, and the opposite when the predicate is always-false.)
     */
    @NotNull
    public static <T> Predicate<T> Negate(Predicate<T> predicate)
        throws ArgumentNullException
    {
        return switch (predicate) {
            case null -> throw new ArgumentNullException("predicate");
            case AlwaysTruePredicate<T> t1 -> new AlwaysFalsePredicate<>();
            case AlwaysFalsePredicate<T> t2 -> new AlwaysTruePredicate<>();
            default -> new NegatedPredicate<>(predicate);
        };
    }

    /**
     * Returns a predicate instance that does always return {@code true}, regardlessly of the value of the predicate's input parameter.
     * @return A new {@link Predicate} that does always return {@code true}.
     * @param <T> Type of the input that will be given to the predicate.
     * @apiNote Note that instances returned through this method have several properties and guarantees:
     * <li>The {@link Predicate#predicate(Object)} method does always return {@code true}.</li>
     * <li>The {@link java.util.function.Predicate#negate()} method does always return an instance obtained through invoking the {@link #AlwaysFalse()} method.</li>
     * <li>Calling {@link java.util.function.Predicate#or(java.util.function.Predicate)} will always return {@code true}, completely ignoring the input parameter.</li>
     * <li>Calling {@link java.util.function.Predicate#and(java.util.function.Predicate)} will always return the value of the input parameter.</li>
     */
    @NotNull
    public static <T> Predicate<T> AlwaysTrue() { return new AlwaysTruePredicate<>(); }

    /**
     * Returns a predicate instance that does always return {@code false}, regardlessly of the value of the predicate's input parameter.
     * @return A new {@link Predicate} that does always return {@code false}.
     * @param <T> Type of the input that will be given to the predicate.
     * @apiNote Note that instances returned through this method have several properties and guarantees:
     * <li>The {@link Predicate#predicate(Object)} method does always return {@code false}.</li>
     * <li>The {@link java.util.function.Predicate#negate()} method does always return an instance obtained through invoking the {@link #AlwaysTrue()} method.</li>
     * <li>Calling {@link java.util.function.Predicate#or(java.util.function.Predicate)} will always return the value of the input parameter.</li>
     * <li>Calling {@link java.util.function.Predicate#and(java.util.function.Predicate)} will always return {@code false}, completely ignoring the input parameter.</li>
     */
    @NotNull
    public static <T> Predicate<T> AlwaysFalse() { return new AlwaysFalsePredicate<>(); }

    /**
     * Provides a {@link Func2} that does always return the input argument.
     * @return A {@link Func2} instance that does always return whatever value was given in it's input.
     * @param <T> The type of the element to be fed as input and to be returned by the function.
     * @apiNote Note that instances returned through this method have several properties and guarantees: 
     * <li>The returned function does always return the value of the input parameter when the function returned is invoked. This, however, means that {@code null} is also returned if the input is {@code null}.</li>
     * <li>Calling {@link Function#compose(Function)} will always return the value of the input parameter.</li>
     * <li>Calling {@link Function#andThen(Function)} will always return the value of the input parameter.</li>
     */
    @NotNull
    public static <T> Func2<T, T> Identity() { return new IdentityFunction<>(); }

    /**
     * Returns a function that accepts elements of type {@link TS} and returns them as {@link TR},
     * if they satisfy the criteria represented by a predicate function.
     * Otherwise, the default value function is called and that is returned instead.
     * @param predicate The predicate that when does return {@code true}, the function maps the
     *                  value to {@link TR} using the provided mapper in the mapper parameter.
     *                  Otherwise, it invokes the default value function.
     * @param mapper The function that maps elements of type {@link TS} to {@link TR}, if {@code predicate} returns {@code true}.
     * @param default_value The function that returns a default value, if {@code predicate} returns {@code false}.
     * @return A function that accepts an input parameter of type {@link TS} and returns a value of type {@link TR}.
     * @param <TS> The type of the input parameter that can be passed in the returned function.
     * @param <TR> The type of the output parameter that can be returned from the returned function.
     * @throws ArgumentNullException {@code predicate} and/or {@code mapper} and/or {@code default_value} are {@code null}.
     */
    @NotNull
    public static <TS, TR> Func2<TS, TR> MapOrDefaultValue(Predicate<TS> predicate, Func2<TS, TR> mapper, Func1<TR> default_value)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(mapper, "mapper");
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        ArgumentNullException.ThrowIfNull(default_value, "default_value");
        return new MapOrDefaultValue<>(predicate, mapper, default_value);
    }

    /**
     * Returns a function that accepts elements of type {@link TS} and returns them as {@link TR},
     * if they satisfy the criteria represented by a predicate function.
     * Otherwise, the default value specified is returned instead.
     * @param predicate The predicate that when does return {@code true}, the function maps the
     *                  value to {@link TR} using the provided mapper in the mapper parameter.
     *                  Otherwise, it invokes the default value function.
     * @param mapper The function that maps elements of type {@link TS} to {@link TR}, if {@code predicate} returns {@code true}.
     * @param default_value A default value of type {@link TS} to return, if {@code predicate} returns {@code false}. Can be {@code null}.
     * @return A function that accepts an input parameter of type {@link TS} and returns a value of type {@link TR}.
     * @param <TS> The type of the input parameter that can be passed in the returned function.
     * @param <TR> The type of the output parameter that can be returned from the returned function.
     * @throws ArgumentNullException {@code predicate} and/or {@code mapper} are {@code null}.
     */
    @NotNull
    public static <TS, TR> Func2<TS, TR> MapOrDefaultValue(Predicate<TS> predicate, Func2<TS, TR> mapper, @AllowNull TR default_value)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(mapper, "mapper");
        ArgumentNullException.ThrowIfNull(predicate, "predicate");
        return new MapOrDefaultValue<>(predicate, mapper, default_value);
    }

    /**
     * Executes the specified action when the provided function is executed by the returned function.
     * This aids to perform an additional action that needs to be done, before returning.
     * @param function The function that upon invoking, the result value will be fed as an input to the {@code consumer} function before returning.
     * @param consumer The function that upon invoking, it receives elements of type {@link TR} computed by {@code function}.
     * @return A function instance wrapping the function specified in {@code function}.
     * @param <TS> The type of the input parameter that can be passed in the returned function.
     * @param <TR> The type of the output parameter that can be returned from the returned function.
     * @throws ArgumentNullException {@code function} and/or {@code consumer} are {@code null}.
     */
    @NotNull
    public static <TS, TR> Func2<TS, TR> ThenConsume(Func2<TS, TR> function, Action1<TR> consumer)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(function, "function");
        ArgumentNullException.ThrowIfNull(consumer, "consumer");
        return new ThenConsume_Func2ToAction1<>(function, consumer);
    }

    /**
     * Executes the specified action when the provided function is executed by the returned function.
     * This aids to perform an additional action that needs to be done, before returning.
     * @param function The function that upon invoking, the result value will be fed as an input to the {@code consumer} function before returning.
     * @param consumer The function that upon invoking, it receives elements of type {@link TR} computed by {@code function}.
     * @return A function instance wrapping the function specified in {@code function}.
     * @param <T1> The type of the first input parameter that can be passed in the returned function.
     * @param <T2> The type of the second input parameter that can be passed in the returned function.
     * @param <TR> The type of the output parameter that can be returned from the returned function.
     * @throws ArgumentNullException {@code function} and/or {@code consumer} are {@code null}.
     */
    @NotNull
    public static <T1, T2, TR> Func3<T1, T2, TR> ThenConsume(Func3<T1, T2, TR> function, Action1<TR> consumer)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(function, "function");
        ArgumentNullException.ThrowIfNull(consumer, "consumer");
        return new ThenConsume_Func3ToAction1<>(function, consumer);
    }

    /**
     * Executes the specified action when the provided function is executed by the returned function.
     * This aids to perform an additional action that needs to be done, before returning.
     * @param function The function that upon invoking, the result value will be fed as an input to the {@code consumer} function before returning.
     * @param consumer The function that upon invoking, it receives elements of type {@link TR} computed by {@code function}.
     * @return A function instance wrapping the function specified in {@code function}.
     * @param <T1> The type of the first input parameter that can be passed in the returned function.
     * @param <T2> The type of the second input parameter that can be passed in the returned function.
     * @param <T3> The type of the third input parameter that can be passed in the returned function.
     * @param <TR> The type of the output parameter that can be returned from the returned function.
     * @throws ArgumentNullException {@code function} and/or {@code consumer} are {@code null}.
     */
    @NotNull
    public static <T1, T2, T3, TR> Func4<T1, T2, T3, TR> ThenConsume(Func4<T1, T2, T3, TR> function, Action1<TR> consumer)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(function, "function");
        ArgumentNullException.ThrowIfNull(consumer, "consumer");
        return new ThenConsume_Func4ToAction1<>(function, consumer);
    }

    /**
     * Executes the specified action when the provided function is executed by the returned function.
     * This aids to perform an additional action that needs to be done, before returning.
     * @param function The function that upon invoking, the result value will be fed as an input to the {@code consumer} function before returning.
     * @param consumer The function that upon invoking, it receives elements of type {@link TR} computed by {@code function}.
     * @return A function instance wrapping the function specified in {@code function}.
     * @param <T1> The type of the first input parameter that can be passed in the returned function.
     * @param <T2> The type of the second input parameter that can be passed in the returned function.
     * @param <T3> The type of the third input parameter that can be passed in the returned function.
     * @param <T4> The type of the fourth input parameter that can be passed in the returned function.
     * @param <TR> The type of the output parameter that can be returned from the returned function.
     * @throws ArgumentNullException {@code function} and/or {@code consumer} are {@code null}.
     */
    @NotNull
    public static <T1, T2, T3, T4, TR> Func5<T1, T2, T3, T4, TR> ThenConsume(Func5<T1, T2, T3, T4, TR> function, Action1<TR> consumer)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(function, "function");
        ArgumentNullException.ThrowIfNull(consumer, "consumer");
        return new ThenConsume_Func5ToAction1<>(function, consumer);
    }

    /**
     * Returns a function that upon invoking it, it first invokes the function provided through the {@code action} parameter,
     * and then executed the function provided through the {@code next} parameter. <br />
     * Note that the {@code next} action will be invoked only if the function of {@code action} parameter has not thrown any exceptions.
     * @param action The base action to execute.
     * @param next The additional action to execute.
     * @return A new function instance as described above.
     * @throws ArgumentNullException {@code action} and/or {@code next} are {@code null}.
     */
    @NotNull
    public static Action0 ThenDo(Action0 action, Action0 next)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(next, "next");
        ArgumentNullException.ThrowIfNull(action, "action");
        return new ThenExecute_Action0(action, next);
    }

    /**
     * Returns a function that upon invoking it, it first invokes the function provided through the {@code action} parameter,
     * and then executed the function provided through the {@code next} parameter. <br />
     * Note that the {@code next} action will be invoked only if the function of {@code action} parameter has not thrown any exceptions.
     * @param action The base action to execute.
     * @param next The additional action to execute.
     * @return A new function instance as described above.
     * @throws ArgumentNullException {@code action} and/or {@code next} are {@code null}.
     */
    @NotNull
    public static <T> Action1<T> ThenDo(Action1<T> action, Action1<T> next)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(next, "next");
        ArgumentNullException.ThrowIfNull(action, "action");
        return new ThenExecute_Action1<>(action, next);
    }

    /**
     * Returns a function that upon invoking it, it first invokes the function provided through the {@code action} parameter,
     * and then executed the function provided through the {@code next} parameter. <br />
     * Note that the {@code next} action will be invoked only if the function of {@code action} parameter has not thrown any exceptions.
     * @param action The base action to execute.
     * @param next The additional action to execute.
     * @return A new function instance as described above.
     * @throws ArgumentNullException {@code action} and/or {@code next} are {@code null}.
     */
    @NotNull
    public static <T1, T2> Action2<T1, T2> ThenDo(Action2<T1, T2> action, Action2<T1, T2> next)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(next, "next");
        ArgumentNullException.ThrowIfNull(action, "action");
        return new ThenExecute_Action2<>(action, next);
    }

    /**
     * Returns a function that upon invoking it, it first invokes the function provided through the {@code action} parameter,
     * and then executed the function provided through the {@code next} parameter. <br />
     * Note that the {@code next} action will be invoked only if the function of {@code action} parameter has not thrown any exceptions.
     * @param action The base action to execute.
     * @param next The additional action to execute.
     * @return A new function instance as described above.
     * @throws ArgumentNullException {@code action} and/or {@code next} are {@code null}.
     */
    @NotNull
    public static <T1, T2, T3> Action3<T1, T2, T3> ThenDo(Action3<T1, T2, T3> action, Action3<T1, T2, T3> next)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(next, "next");
        ArgumentNullException.ThrowIfNull(action, "action");
        return new ThenExecute_Action3<>(action, next);
    }

    /**
     * Returns a function that maps elements of type {@link TS} to {@link TR},
     * by first invoking a mapper that maps elements of type {@link TS} to type {@link TM},
     * and then invoking a mapper that maps elements of type {@link TM} to type {@link TR}.
     * @param first The first mapping function that maps elements of type {@link TS} to type {@link TM}.
     * @param second The second mapping function that maps elements of type {@link TM} to type {@link TR}.
     * @return A function that maps elements of type {@link TS} to {@link TR}.
     * @param <TS> The type of the input to map to {@link TR}.
     * @param <TM> The type of intermediate output of the first function that is passed as a parameter to the second function.
     * @param <TR> The type of the output that is the result of mapping {@link TS} to {@link TR}.
     * @throws ArgumentNullException {@code first} and/or {@code second} is {@code null}.
     */
    @NotNull
    public static <TS, TM, TR> Func2<TS, TR> ThenMap(Func2<TS, TM> first, Func2<TM, TR> second)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(first, "first");
        ArgumentNullException.ThrowIfNull(second, "second");
        return new MapTwoFunctions_Func2<>(first, second);
    }

    /**
     * Returns a function that maps the input arguments of type {@link T1} and {@link T2} to {@link TR},
     * by first invoking a mapper that maps the input arguments of type {@link T1} and {@link T2} to type {@link TM},
     * and then invoking a mapper that maps elements of type {@link TM} to type {@link TR}.
     * @param first The first mapping function that maps elements of type {@link TS} to type {@link TM}.
     * @param second The second mapping function that maps elements of type {@link TM} to type {@link TR}.
     * @return A function that maps elements input arguments of type {@link T1} and {@link T2} to {@link TR}.
     * @param <T1> The type of the first argument to map to {@link TR}.
     * @param <T2> The type of the second argument to map to {@link TR}.
     * @param <TM> The type of intermediate output of the first function that is passed as a parameter to the second function.
     * @param <TR> The type of the output that is the result of mapping {@link T1} and {@link T2} to {@link TR}.
     * @throws ArgumentNullException {@code first} and/or {@code second} is {@code null}.
     */
    @NotNull
    public static <T1, T2, TM, TR> Func3<T1, T2, TR> ThenMap(Func3<T1, T2, TM> first, Func2<TM, TR> second)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(first, "first");
        ArgumentNullException.ThrowIfNull(second, "second");
        return new MapTwoFunctions_Func3Func2<>(first, second);
    }
}
