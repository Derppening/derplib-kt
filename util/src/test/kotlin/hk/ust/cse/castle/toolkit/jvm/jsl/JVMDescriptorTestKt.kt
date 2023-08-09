package hk.ust.cse.castle.toolkit.jvm.jsl

import kotlin.test.Test
import kotlin.test.assertEquals

class JVMDescriptorTestKt {

    @Test
    fun `Field Serialization from Builder - Java Primitive Type`() {
        val intPrimitiveDescriptor = FieldDescriptor<Int>()
        val intWrapperDescriptor = FieldDescriptor<Int>(objectTypeOnly = true)

        assertEquals("I", intPrimitiveDescriptor.toDescriptorString())
        assertEquals("Ljava/lang/Integer;", intWrapperDescriptor.toDescriptorString())
    }

    @Test
    fun `Field Serialization from Builder - Java Object`() {
        val descriptor = FieldDescriptor<Any>()

        assertEquals("Ljava/lang/Object;", descriptor.toDescriptorString())
    }

    @Test
    fun `Field Serialization from Builder - Multidimensional Array`() {
        val descriptor = FieldDescriptor<DoubleArray>(arrayDims = 2)

        assertEquals("[[[D", descriptor.toDescriptorString())
    }

    @Test
    fun `Method Serialization from Builder - void()`() {
        val descriptor = MethodDescriptor()

        assertEquals("()V", descriptor.toDescriptorString())
    }

    @Test
    fun `Method Serialization from Builder - void() using Unit`() {
        val descFromKClass = JVMDescriptor.MethodDescriptor.Builder()
            .setReturnType(Unit::class)
            .build()
        val descFromReified = JVMDescriptor.MethodDescriptor.Builder()
            .setReturnType<Unit>()
            .build()

        assertEquals("()Lkotlin/Unit;", descFromKClass.toDescriptorString())
        assertEquals("()Lkotlin/Unit;", descFromReified.toDescriptorString())
    }

    @Test
    fun `Method Serialization from Builder - Non-Trivial Method`() {
        val descriptor = MethodDescriptor<Any>(Int::class, Double::class, Thread::class)

        assertEquals("(IDLjava/lang/Thread;)Ljava/lang/Object;", descriptor.toDescriptorString())
    }
}