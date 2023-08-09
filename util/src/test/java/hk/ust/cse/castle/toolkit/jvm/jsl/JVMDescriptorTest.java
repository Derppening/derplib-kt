package hk.ust.cse.castle.toolkit.jvm.jsl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JVMDescriptorTest {

    @Test
    void testSerializePrimitiveAndWrapperClasses() {
        final JVMDescriptor primitiveDescriptor = JVMDescriptor.FieldDescriptor.fromClass(int.class);
        final JVMDescriptor wrapperDescriptor = JVMDescriptor.FieldDescriptor.fromClass(Integer.class);

        assertEquals("I", primitiveDescriptor.toDescriptorString());
        assertEquals("Ljava/lang/Integer;", wrapperDescriptor.toDescriptorString());
    }

    @Test
    void testSerializePrimitiveAndWrapperMethods() {
        final JVMDescriptor primitiveParamDescriptor = new JVMDescriptor.MethodDescriptor.Builder()
                .addParameter(int.class)
                .build();
        final JVMDescriptor wrapperParamDescriptor = new JVMDescriptor.MethodDescriptor.Builder()
                .addParameter(Integer.class)
                .build();
        final JVMDescriptor primitiveReturnTypeDescriptor = new JVMDescriptor.MethodDescriptor.Builder()
                .setReturnType(int.class)
                .build();
        final JVMDescriptor wrapperReturnTypeDescriptor = new JVMDescriptor.MethodDescriptor.Builder()
                .setReturnType(Integer.class)
                .build();

        assertEquals("(I)V", primitiveParamDescriptor.toDescriptorString());
        assertEquals("(Ljava/lang/Integer;)V", wrapperParamDescriptor.toDescriptorString());
        assertEquals("()I", primitiveReturnTypeDescriptor.toDescriptorString());
        assertEquals("()Ljava/lang/Integer;", wrapperReturnTypeDescriptor.toDescriptorString());
    }

    @Test
    void testSerializeJavaObjectFromClass() {
        final JVMDescriptor descriptor = JVMDescriptor.FieldDescriptor.fromClass(Object.class);

        assertEquals("Ljava/lang/Object;", descriptor.toDescriptorString());
    }

    @Test
    void testSerializeJavaObject() {
        final JVMDescriptor descriptor = new JVMDescriptor.FieldDescriptor.Builder(Object.class)
                .build();

        assertEquals("Ljava/lang/Object;", descriptor.toDescriptorString());
    }

    @Test
    void testSerializeMultiDimArrayFromClass() {
        final JVMDescriptor descriptor = JVMDescriptor.FieldDescriptor.fromClass(double[][][].class);

        assertEquals("[[[D", descriptor.toDescriptorString());
    }

    @Test
    void testSerializeMultiDimArray() {
        final JVMDescriptor descriptor = new JVMDescriptor.FieldDescriptor.Builder(double.class)
                .increaseArrayDim(3)
                .build();

        assertEquals("[[[D", descriptor.toDescriptorString());
    }

    @Test
    void testSerializeVoidMethodNoArgs() {
        final JVMDescriptor descriptor = new JVMDescriptor.MethodDescriptor.Builder()
                .build();

        assertEquals("()V", descriptor.toDescriptorString());
    }

    @Test
    void testSerializeNonTrivialMethod() {
        final JVMDescriptor descriptor = new JVMDescriptor.MethodDescriptor.Builder()
                .addParameters(int.class, double.class, Thread.class)
                .setReturnType(Object.class)
                .build();

        assertEquals("(IDLjava/lang/Thread;)Ljava/lang/Object;", descriptor.toDescriptorString());
    }

    @Test
    void testSerializeMainMethod() {
        final JVMDescriptor descriptor = new JVMDescriptor.MethodDescriptor.Builder()
                .addParameter(String[].class)
                .build();

        assertEquals("([Ljava/lang/String;)V", descriptor.toDescriptorString());
    }
}
