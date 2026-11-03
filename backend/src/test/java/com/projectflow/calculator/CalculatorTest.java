package com.projectflow.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Calculator class.
 */
public class CalculatorTest {
    private Calculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new Calculator();
    }

    @Test
    public void testAdd() {
        assertEquals(8.0, calculator.add(5, 3));
        assertEquals(0.0, calculator.add(-5, 5));
        assertEquals(-2.0, calculator.add(-5, 3));
    }

    @Test
    public void testSubtract() {
        assertEquals(2.0, calculator.subtract(5, 3));
        assertEquals(-10.0, calculator.subtract(-5, 5));
        assertEquals(-8.0, calculator.subtract(-5, 3));
    }

    @Test
    public void testMultiply() {
        assertEquals(15.0, calculator.multiply(5, 3));
        assertEquals(-15.0, calculator.multiply(-5, 3));
        assertEquals(25.0, calculator.multiply(-5, -5));
    }

    @Test
    public void testDivide() {
        assertEquals(2.5, calculator.divide(5, 2));
        assertEquals(-1.0, calculator.divide(-5, 5));
        assertEquals(1.0, calculator.divide(5, 5));
    }

    @Test
    public void testDivideByZero() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> calculator.divide(5, 0)
        );
        assertEquals("Cannot divide by zero", exception.getMessage());
    }

    @Test
    public void testPower() {
        assertEquals(8.0, calculator.power(2, 3));
        assertEquals(1.0, calculator.power(5, 0));
        assertEquals(0.25, calculator.power(2, -2));
    }

    @Test
    public void testSquareRoot() {
        assertEquals(3.0, calculator.squareRoot(9));
        assertEquals(5.0, calculator.squareRoot(25));
        assertEquals(0.0, calculator.squareRoot(0));
    }

    @Test
    public void testSquareRootNegative() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> calculator.squareRoot(-4)
        );
        assertEquals("Cannot calculate square root of a negative number", exception.getMessage());
    }

    @Test
    public void testAbsoluteValue() {
        assertEquals(5.0, calculator.absoluteValue(5));
        assertEquals(5.0, calculator.absoluteValue(-5));
        assertEquals(0.0, calculator.absoluteValue(0));
    }
}
