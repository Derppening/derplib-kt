package hk.ust.cse.castle.toolkit.jvm.jsl

import hk.ust.cse.castle.toolkit.jvm.util.JavaApi
import java.util.*
import kotlin.reflect.KClass

/**
 * An instance representing a component within a JVM descriptor.
 */
private interface DescriptorComponent {

    /**
     * @return The descriptor string of this descriptor component.
     */
    fun toDescriptorString(): String
}

/**
 * A descriptor representing the type of a JVM field or method.
 *
 * Information regarding JVM descriptors can be found in the JVM specification manual.
 */
sealed class JVMDescriptor : DescriptorComponent {

    /**
     * A descriptor representing a JVM field.
     *
     * @param descriptorType The [type of descriptor][descriptorType] of this field.
     */
    private sealed class FieldType(protected val descriptorType: DescriptorType) : DescriptorComponent {

        /**
         * Types of field descriptors.
         *
         * Descriptions of all field descriptors are copied from
         * [JVM Specification for Java 11](https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.3.2).
         *
         * @property formatStr The format string from which to generate the descriptor string.
         * @property numPlaceholders Number of placeholders for this field descriptor.
         */
        enum class DescriptorType(private val formatStr: String, private val numPlaceholders: Int = 0) {

            /**
             * `byte` - Signed byte.
             *
             * Represented by `B`.
             */
            BYTE("B"),

            /**
             * `char` - Unicode character code point in the Basic Multilingual Plane, encoded with UTF-16.
             *
             * Represented by `C`.
             */
            CHAR("C"),

            /**
             * `double` - Double-precision floating-point value.
             *
             * Represented by `D`.
             */
            DOUBLE("D"),

            /**
             * `float` - Single-precision floating-point value.
             *
             * Represented by `F`.
             */
            FLOAT("F"),

            /**
             * `int` - Integer.
             *
             * Represented by `I`.
             */
            INT("I"),

            /**
             * `long` - Long integer.
             *
             * Represented by `J`.
             */
            LONG("J"),

            /**
             * `reference` - An instance of class *ClassName*.
             *
             * Represented by `L` *ClassName* `;`.
             */
            REFERENCE("L%s;", 1),

            /**
             * `short` - Signed short.
             *
             * Represented by `S`.
             */
            SHORT("S"),

            /**
             * `boolean` - `true` or `false`.
             *
             * Represented by `Z`.
             */
            BOOLEAN("Z"),

            /**
             * `reference` - One array dimension.
             *
             * Represented by `[`.
             */
            ARRAY("[%s", 1);

            /**
             * Substitutes placeholders of this descriptor with concrete values.
             *
             * The number of arguments to this method must match the number of placeholders of this field descriptor,
             * otherwise an [IllegalArgumentException] will be thrown.
             */
            fun substituteValues(vararg placeholders: String): String {
                require(placeholders.size == numPlaceholders)

                return String.format(formatStr, *placeholders)
            }
        }

        companion object {

            /**
             * Creates a [FieldType] from a [Java Class instance][clazz].
             */
            @JvmStatic
            fun fromClass(clazz: Class<*>): FieldType {
                return when {
                    clazz.isPrimitive -> BaseType.fromClass(clazz)
                    clazz.isArray -> ArrayType(ComponentType(fromClass(clazz.componentType)))
                    else -> ObjectType.fromClass(clazz)
                }
            }

            /**
             * Creates a [FieldType] from a [Kotlin Class instance][clazz].
             *
             * @param objectTypeOnly If true and [clazz] is a primitive type, creates a field descriptor using the
             * wrapper class instead.
             */
            @JvmStatic
            fun fromKClass(clazz: KClass<*>, objectTypeOnly: Boolean): FieldType =
                fromClass(if (objectTypeOnly) clazz.javaObjectType else clazz.java)
        }
    }

    /**
     * Base field type, i.e. primitive types.
     */
    private class BaseType private constructor(descriptorType: DescriptorType) : FieldType(descriptorType) {

        init {
            check(descriptorType in allowedTypes) { "DescriptorType[$descriptorType] is not a BaseType" }
        }

        override fun toDescriptorString(): String = descriptorType.substituteValues()

        companion object {

            /**
             * [Descriptor types][FieldType.DescriptorType] which are legal `BaseType`s.
             */
            private val allowedTypes = arrayOf(
                DescriptorType.BYTE,
                DescriptorType.CHAR,
                DescriptorType.DOUBLE,
                DescriptorType.FLOAT,
                DescriptorType.INT,
                DescriptorType.LONG,
                DescriptorType.SHORT,
                DescriptorType.BOOLEAN
            )

            /**
             * Creates a [BaseType] from a [class name][clazzName].
             *
             * @throws IllegalArgumentException if the [descriptorType] is not a valid `BaseType`.
             */
            fun fromClassName(clazzName: String): BaseType {
                val descriptorType = allowedTypes
                    .singleOrNull { it.name.lowercase(Locale.ENGLISH) == clazzName }

                requireNotNull(descriptorType) { "$clazzName does not represent a primitive type" }
                return BaseType(descriptorType)
            }

            /**
             * Creates a [BaseType] from a [Java class instance][clazz].
             *
             * @throws IllegalArgumentException if [clazz] is not a valid `BaseType`.
             */
            fun fromClass(clazz: Class<*>): BaseType = fromClassName(clazz.name)
        }
    }

    /**
     * Object field type, i.e. reference types.
     *
     * @param internalName The internal name of the class, i.e. the binary name of the class with all ASCII periods
     * (`.`) replaced by ASCII forward slashes (`/`).
     */
    private class ObjectType private constructor(private val internalName: String) :
        FieldType(DescriptorType.REFERENCE) {

        override fun toDescriptorString(): String = descriptorType.substituteValues(internalName)

        companion object {

            /**
             * Creates a [ObjectType] from a [Java class instance][clazz].
             *
             * @throws IllegalArgumentException if [clazz] is an array type or primitive type.
             */
            fun fromClass(clazz: Class<*>): ObjectType {
                require(!clazz.isArray && !clazz.isPrimitive) {
                    "ObjectType cannot be used for array or primitive types"
                }

                return ObjectType(clazz.name.replace('.', '/'))
            }
        }
    }

    /**
     * Array field type.
     *
     * @param componentType The nested component of the array, i.e. the one dimension-removed type of this array.
     */
    private class ArrayType(private val componentType: ComponentType) : FieldType(DescriptorType.ARRAY) {

        override fun toDescriptorString(): String = descriptorType.substituteValues(componentType.toDescriptorString())
    }

    /**
     * Component type; represents the nested type of an array.
     */
    private class ComponentType(private val fieldType: FieldType) : DescriptorComponent by fieldType

    /**
     * Parameter descriptor; represents a single parameter type.
     */
    private class ParameterDescriptor(private val fieldType: FieldType) : DescriptorComponent by fieldType

    /**
     * Return type descriptor; represents a possibly-`void` return type.
     */
    private class ReturnDescriptor(private val fieldType: FieldType? = null) : DescriptorComponent {

        override fun toDescriptorString(): String = (fieldType ?: VoidDescriptor).toDescriptorString()
    }

    /**
     * Return type descriptor indicating that the method returned no value.
     */
    private object VoidDescriptor : DescriptorComponent {

        override fun toDescriptorString(): String = "V"
    }

    /**
     * From
     * [JVM Specification for Java 11](https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.3.2):
     * A *field descriptor* represents the type of a class, instance, or local variable.
     *
     * Users should instantiate this class via [FieldDescriptor.fromClass] to build a descriptor from an existing class,
     * or [FieldDescriptor.Builder] to build a field descriptor from scratch.
     *
     * Note that this class does not verify that an array descriptor has fewer than 256 dimensions (as per the JVM
     * specification).
     */
    class FieldDescriptor private constructor(private val fieldType: FieldType) : JVMDescriptor() {

        class Builder {

            private var type: FieldType? = null

            @Deprecated("Use the single-argument constructor to initialize the base type.")
            constructor()

            /**
             * Creates a [Builder] instance with [baseType] as the base type of the field.
             */
            @JavaApi(ReplaceWith("Builder(clazz.kotlin)"))
            constructor(baseType: Class<*>) {
                type = FieldType.fromClass(baseType)
            }

            /**
             * Creates [Builder] instance with [baseType] as the base type of the field.
             *
             * @param objectTypeOnly If true and [baseType] is a primitive type, creates a field descriptor using the
             * wrapper class instead.
             */
            @JvmOverloads
            constructor(baseType: KClass<*>, objectTypeOnly: Boolean = false) {
                type = FieldType.fromKClass(baseType, objectTypeOnly)
            }

            /**
             * Sets the base type of the field.
             *
             * @param clazz [Class] instance representing the base type of the field.
             * @return `this`
             */
            @JavaApi(ReplaceWith("setBaseType(clazz.kotlin)"))
            @Deprecated("Use the single-argument constructor to initialize the base type.")
            fun setBaseType(clazz: Class<*>): Builder {
                type = FieldType.fromClass(clazz)
                return this
            }

            /**
             * Sets the base type of the field.
             *
             * @param clazz [KClass] instance representing the base type of the field.
             * @param objectTypeOnly If true and [clazz] is a primitive type, creates a field descriptor using the
             * wrapper class instead.
             * @return `this`
             */
            @JvmOverloads
            @Deprecated("Use the single-argument constructor to initialize the base type.")
            fun setBaseType(clazz: KClass<*>, objectTypeOnly: Boolean = false): Builder {
                type = FieldType.fromKClass(clazz, objectTypeOnly)
                return this
            }

            /**
             * Sets the base type of the field.
             *
             * @param T Base type of the field.
             * @param objectTypeOnly If true and [T] is a primitive type, creates a field descriptor using the wrapper
             * class instead.
             * @return `this`
             */
            @Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
            @Deprecated("Use the single-argument constructor to initialize the base type.")
            inline fun <reified T> setBaseType(objectTypeOnly: Boolean = true): Builder =
                setBaseType(T::class, objectTypeOnly)

            /**
             * Increments the array dimension of this field.
             *
             * @param count The number of array dimensions to increment.
             * @return `this`
             */
            @JvmOverloads
            fun increaseArrayDim(count: Int = 1): Builder {
                repeat(count) {
                    type = ArrayType(ComponentType(checkNotNull(type)))
                }
                return this
            }

            fun build(): FieldDescriptor = FieldDescriptor(checkNotNull(type))
        }

        override fun toDescriptorString(): String = fieldType.toDescriptorString()

        companion object {

            /**
             * Creates an instance of [FieldDescriptor] from a known class.
             *
             * @param clazz [Class] instance to create a descriptor from.
             * @return [FieldDescriptor] instance representing this class.
             */
            @JavaApi(ReplaceWith("fromClass(clazz.kotlin)"))
            @JvmStatic
            fun fromClass(clazz: Class<*>): FieldDescriptor {
                return FieldDescriptor(FieldType.fromClass(clazz))
            }

            /**
             * Creates an instance of [FieldDescriptor] from a known class.
             *
             * @param clazz [KClass] instance to create a descriptor from.
             * @param objectTypeOnly If true and [clazz] is a primitive type, creates a field descriptor using the
             * wrapper class instead.
             * @return [FieldDescriptor] instance representing this class.
             */
            @JvmStatic
            @JvmOverloads
            fun fromClass(clazz: KClass<*>, objectTypeOnly: Boolean = false): FieldDescriptor {
                return FieldDescriptor(FieldType.fromKClass(clazz, objectTypeOnly))
            }
        }
    }

    /**
     * From
     * [JVM Specification for Java 11](https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-4.html#jvms-4.3.3):
     * A *method descriptor* contains zero or more *parameter descriptors*, representing the types of parameters that
     * the method takes, and a *return descriptor*, representing the type of the value (if any) that the method returns.
     *
     * Users should instantiate this class via [MethodDescriptor.Builder].
     */
    class MethodDescriptor private constructor(
        private val paramDesc: Collection<ParameterDescriptor>,
        private val returnDesc: ReturnDescriptor
    ) : JVMDescriptor() {

        class Builder {

            private val params = mutableListOf<ParameterDescriptor>()
            private var returnDesc = ReturnDescriptor()

            /**
             * Adds a parameter to this descriptor.
             *
             * @param clazz [Class] instance representing the parameter type.
             * @return `this`.
             */
            @JavaApi(ReplaceWith("addParameter(clazz.kotlin)"))
            fun addParameter(clazz: Class<*>): Builder {
                params.add(ParameterDescriptor(FieldType.fromClass(clazz)))
                return this
            }

            /**
             * Adds a parameter to this descriptor.
             *
             * @param clazz [KClass] instance representing the parameter type.
             * @param objectTypeOnly If true and [clazz] is a primitive type, creates a field descriptor using the
             * wrapper class instead.
             * @return `this`.
             */
            @JvmOverloads
            fun addParameter(clazz: KClass<*>, objectTypeOnly: Boolean = false): Builder {
                params.add(ParameterDescriptor(FieldType.fromKClass(clazz, objectTypeOnly)))
                return this
            }

            /**
             * Adds a parameter to this descriptor.
             *
             * @param T Class of the parameter type.
             * @param objectTypeOnly If true and [T] is a primitive type, creates a field descriptor using the wrapper
             * class instead.
             * @return `this`.
             */
            inline fun <reified T> addParameter(objectTypeOnly: Boolean = false): Builder =
                addParameter(T::class, objectTypeOnly)

            /**
             * Adds multiple parameters to this descriptor.
             *
             * @param clazz [Class] instances representing the parameter type.
             * @return `this`.
             */
            @JavaApi(ReplaceWith("addParameters(clazz.kotlin)"))
            fun addParameters(vararg clazz: Class<*>): Builder {
                clazz.forEach { addParameter(it) }
                return this
            }

            /**
             * Adds multiple parameters to this descriptor.
             *
             * @param clazz [KClass] instances representing the parameter type.
             * @return `this`.
             */
            fun addParameters(vararg clazz: KClass<*>): Builder {
                clazz.forEach { addParameter(it) }
                return this
            }

            /**
             * Set this builder to create a method descriptor returning `void`.
             *
             * @return `this`.
             */
            fun setVoidReturnType(): Builder {
                returnDesc = ReturnDescriptor()
                return this
            }

            /**
             * Set this builder to create a method descriptor returning the given class type.
             *
             * @param clazz [Class] instance representing the return type.
             * @return `this`.
             */
            @JavaApi(ReplaceWith("setReturnType(clazz.kotlin)"))
            fun setReturnType(clazz: Class<*>): Builder {
                returnDesc = ReturnDescriptor(FieldType.fromClass(clazz))
                return this
            }

            /**
             * Set this builder to create a method descriptor returning the given class type.
             *
             * Note that passing [Unit] to this method will set the return type to `Lkotlin/Unit;`. Use
             * [setVoidReturnType] if a method with no return value is desired.
             *
             * @param clazz [KClass] instance representing the return type.
             * @param objectTypeOnly If true and [clazz] is a primitive type, creates a field descriptor using the
             * wrapper class instead.
             * @return `this`.
             */
            @JvmOverloads
            fun setReturnType(clazz: KClass<*>, objectTypeOnly: Boolean = false): Builder {
                returnDesc = ReturnDescriptor(FieldType.fromKClass(clazz, objectTypeOnly))
                return this
            }

            /**
             * Set this builder to create a method descriptor returning the given class type.
             *
             * Note that passing [Unit] to this method will set the return type to `Lkotlin/Unit;`. Use
             * [setVoidReturnType] if a method with no return value is desired.
             *
             * @param T Type of the return type.
             * @param objectTypeOnly If true and [T] is a primitive type, creates a field descriptor using the wrapper
             * class instead.
             * @return `this`.
             */
            inline fun <reified T> setReturnType(objectTypeOnly: Boolean = false): Builder =
                setReturnType(T::class, objectTypeOnly)

            fun build(): MethodDescriptor = MethodDescriptor(params, returnDesc)
        }

        override fun toDescriptorString(): String =
            "(${paramDesc.joinToString("") { it.toDescriptorString() }})${returnDesc.toDescriptorString()}"
    }
}

/**
 * Creates a [JVMDescriptor.FieldDescriptor] from the [given type][T], wrapping the type in the given number of
 * [array dimensions][arrayDims].
 *
 * @param objectTypeOnly If true and [T] is a primitive type, creates a field descriptor using the wrapper class
 * instead.
 */
@Suppress("FunctionName")
inline fun <reified T : Any> FieldDescriptor(
    objectTypeOnly: Boolean = false,
    arrayDims: Int = 0
): JVMDescriptor.FieldDescriptor {
    val resolvedClass = T::class.javaPrimitiveType?.takeUnless { objectTypeOnly }
        ?: T::class.java

    return JVMDescriptor.FieldDescriptor.Builder(resolvedClass)
        .increaseArrayDim(arrayDims)
        .build()
}

/**
 * Creates a [JVMDescriptor.MethodDescriptor] with the given [parameter types][paramTypes] and returning `void`.
 *
 * Note that this method will set primitive [KClass] using the primitive representations (e.g. `int`) rather than the
 * object representations (e.g. [java.lang.Integer]). If the latter is desired, use
 * [JVMDescriptor.MethodDescriptor.Builder] instead.
 */
@Suppress("FunctionName")
@JvmName("MethodDescriptorKtVoid")
fun MethodDescriptor(vararg paramTypes: KClass<*>): JVMDescriptor.MethodDescriptor {
    return JVMDescriptor.MethodDescriptor.Builder()
        .addParameters(*paramTypes)
        .setVoidReturnType()
        .build()
}

/**
 * Creates a [JVMDescriptor.MethodDescriptor] with the given [parameter types][paramTypes] and returning [R].
 *
 * Note that this method will set primitive [KClass] using the primitive representations (e.g. `int`) rather than the
 * object representations (e.g. [java.lang.Integer]). If the latter is desired, use
 * [JVMDescriptor.MethodDescriptor.Builder] instead.
 */
@Suppress("FunctionName")
@JvmName("MethodDescriptorKtVararg")
inline fun <reified R> MethodDescriptor(vararg paramTypes: KClass<*>): JVMDescriptor.MethodDescriptor {
    return JVMDescriptor.MethodDescriptor.Builder()
        .addParameters(*paramTypes)
        .setReturnType<R>()
        .build()
}
