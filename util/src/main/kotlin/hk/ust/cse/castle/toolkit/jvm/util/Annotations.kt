package hk.ust.cse.castle.toolkit.jvm.util

/**
 * Marks the annotated method as a Java API.
 *
 * A Java API is an API that is intended for use by Java code.
 *
 * Normally, Java code is able to use the Kotlin-implemented library methods via Java/Kotlin interop, however there are
 * cases where these interop are not idiomatic for either side, for example:
 *
 * - Lambdas of type `(...) -> Unit` must return an instance of [Unit] when used by Java code.
 * - Extension methods in Kotlin are compiled with an additional implicit argument, which harms the readability of
 * parameter names when used in Java.
 *
 * In these cases, this annotation can be used to indicate that a more idiomatic Kotlin expression exists for Kotlin
 * code, and should be preferred over this method.
 *
 * @property kotlinExpr The Kotlin expression which can be used in-place of this method. The [ReplaceWith] annotation
 * should be used in the same way as of [Deprecated].
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR)
@MustBeDocumented
annotation class JavaApi(val kotlinExpr: ReplaceWith)