package hk.ust.cse.castle.toolkit.jvm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ByteUnitTest {

    @Test
    @DisplayName("Units Test - Magnitude Values")
    void testUnitValues() {
        assertEquals(1L, ByteUnit.BYTE.scale);
        assertEquals(1_000L, ByteUnit.KILOBYTE.scale);
        assertEquals(1024L, ByteUnit.KIBIBYTE.scale);
        assertEquals(1_000_000L, ByteUnit.MEGABYTE.scale);
        assertEquals(1024L * 1024L, ByteUnit.MEBIBYTE.scale);
        assertEquals(1_000_000_000L, ByteUnit.GIGABYTE.scale);
        assertEquals(1024L * 1024L * 1024L, ByteUnit.GIBIBYTE.scale);
        assertEquals(1_000_000_000_000L, ByteUnit.TERABYTE.scale);
        assertEquals(1024L * 1024L * 1024L * 1024L, ByteUnit.TEBIBYTE.scale);
    }

    @Test
    @DisplayName("Units Test - Conversion with Integral Results")
    void testUnitConvertToIntegral() {
        assertEquals(1L, ByteUnit.KILOBYTE.convertIntegral(1000));
        assertEquals(1.0, ByteUnit.KILOBYTE.convertReal(1000));
    }

    @Test
    @DisplayName("Units Test - Conversion with Floating-Point Results")
    void testUnitConvertToFP() {
        assertEquals(1L, ByteUnit.KILOBYTE.convertIntegral(1500));
        assertEquals(1.5, ByteUnit.KILOBYTE.convertReal(1500));
    }

    @Test
    @DisplayName("Units Test - Scaling as Integer")
    void testUnitScaleToIntegral() {
        assertEquals(1000L, ByteUnit.KILOBYTE.scaleIntegral(1));
        assertEquals(10240L, ByteUnit.KIBIBYTE.scaleIntegral(10));
    }

    @Test
    @DisplayName("Units Test - Scaling as Floating-Point")
    void testUnitScaleToFP() {
        assertEquals(1500.0, ByteUnit.KILOBYTE.scaleReal(1.5));
        assertEquals(1536.0, ByteUnit.KIBIBYTE.scaleReal(1.5));
    }

    @Test
    @DisplayName("Prefix Scale Test - Scale Values")
    void testPrefixScaleValues() {
        assertEquals(1000L, ByteUnit.PrefixScale.SI.value);
        assertEquals(1024L, ByteUnit.PrefixScale.IEC.value);
    }

    @Test
    @DisplayName("Prefix Scale Test - Negative Power")
    void testPrefixScalePowNegative() {
        assertThrows(IllegalArgumentException.class, () -> ByteUnit.PrefixScale.SI.pow(-1));
        assertThrows(IllegalArgumentException.class, () -> ByteUnit.PrefixScale.IEC.pow(-1));
    }

    @Test
    @DisplayName("Prefix Scale Test - Power of Zero")
    void testPrefixScalePowZero() {
        assertEquals(1L, ByteUnit.PrefixScale.SI.pow(0));
        assertEquals(1L, ByteUnit.PrefixScale.IEC.pow(0));
    }

    @Test
    @DisplayName("Prefix Scale Test - Power of One")
    void testPrefixScalePowOne() {
        assertEquals(ByteUnit.PrefixScale.SI.value, ByteUnit.PrefixScale.SI.pow(1));
        assertEquals(ByteUnit.PrefixScale.IEC.value, ByteUnit.PrefixScale.IEC.pow(1));
    }

    @Test
    @DisplayName("Prefix Scale Test - Power of Two")
    void testPrefixScalePowTwo() {
        assertEquals((long) Math.pow(ByteUnit.PrefixScale.SI.value, 2), ByteUnit.PrefixScale.SI.pow(2));
        assertEquals((long) Math.pow(ByteUnit.PrefixScale.IEC.value, 2), ByteUnit.PrefixScale.IEC.pow(2));
    }

    @Test
    @DisplayName("Prefix Scale Test - Max Power")
    void testPrefixScalePowMax() {
        assertEquals((long) Math.pow(ByteUnit.PrefixScale.SI.value, 6), ByteUnit.PrefixScale.SI.pow(6));
        assertEquals((long) Math.pow(ByteUnit.PrefixScale.IEC.value, 6), ByteUnit.PrefixScale.IEC.pow(6));
    }

    @Test
    @DisplayName("Prefix Scale Test - Result Overflow")
    void testPrefixScalePowOverflow() {
        assertThrows(IllegalArgumentException.class, () -> ByteUnit.PrefixScale.SI.pow(7));
        assertThrows(IllegalArgumentException.class, () -> ByteUnit.PrefixScale.IEC.pow(7));
    }
}
