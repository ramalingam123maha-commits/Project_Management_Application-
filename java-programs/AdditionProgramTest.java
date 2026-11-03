/**
 * AdditionProgramTest - Unit tests for AdditionProgram
 * 
 * Tests all addition methods with various inputs including:
 * - Basic two-number addition
 * - Multiple number addition
 * - Array addition
 * - Average calculation
 * - Edge cases and error handling
 * 
 * @author Bold Create Agent
 * @version 1.0
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class AdditionProgramTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    /**
     * Assert that two values are equal
     */
    private static void assertEquals(String testName, Object expected, Object actual) {
        if (expected == null ? actual == null : expected.equals(actual)) {
            System.out.println("✓ PASS: " + testName);
            testsPassed++;
        } else {
            System.out.println("✗ FAIL: " + testName);
            System.out.println("  Expected: " + expected);
            System.out.println("  Actual: " + actual);
            testsFailed++;
        }
    }

    /**
     * Assert that a condition is true
     */
    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("✓ PASS: " + testName);
            testsPassed++;
        } else {
            System.out.println("✗ FAIL: " + testName);
            testsFailed++;
        }
    }

    /**
     * Test: Add two positive integers
     */
    public static void testAddTwoPositiveIntegers() {
        int result = AdditionProgram.add(5, 3);
        assertEquals("Add two positive integers (5 + 3)", 8, result);
    }

    /**
     * Test: Add two negative integers
     */
    public static void testAddTwoNegativeIntegers() {
        int result = AdditionProgram.add(-5, -3);
        assertEquals("Add two negative integers (-5 + -3)", -8, result);
    }

    /**
     * Test: Add positive and negative integers
     */
    public static void testAddMixedSignIntegers() {
        int result = AdditionProgram.add(10, -3);
        assertEquals("Add mixed sign integers (10 + -3)", 7, result);
    }

    /**
     * Test: Add zero
     */
    public static void testAddZero() {
        int result = AdditionProgram.add(5, 0);
        assertEquals("Add with zero (5 + 0)", 5, result);
    }

    /**
     * Test: Add two zeros
     */
    public static void testAddTwoZeros() {
        int result = AdditionProgram.add(0, 0);
        assertEquals("Add two zeros (0 + 0)", 0, result);
    }

    /**
     * Test: Add two positive doubles
     */
    public static void testAddTwoPositiveDoubles() {
        double result = AdditionProgram.add(5.5, 3.2);
        assertEquals("Add two positive doubles (5.5 + 3.2)", 8.7, Math.round(result * 10) / 10.0);
    }

    /**
     * Test: Add two negative doubles
     */
    public static void testAddTwoNegativeDoubles() {
        double result = AdditionProgram.add(-5.5, -3.2);
        assertEquals("Add two negative doubles (-5.5 + -3.2)", -8.7, Math.round(result * 10) / 10.0);
    }

    /**
     * Test: Add multiple positive integers
     */
    public static void testAddMultiplePositiveIntegers() {
        int result = AdditionProgram.addMultiple(1, 2, 3, 4, 5);
        assertEquals("Add multiple positive integers (1+2+3+4+5)", 15, result);
    }

    /**
     * Test: Add multiple mixed integers
     */
    public static void testAddMultipleMixedIntegers() {
        int result = AdditionProgram.addMultiple(10, -5, 8, -3, 2);
        assertEquals("Add multiple mixed integers (10-5+8-3+2)", 12, result);
    }

    /**
     * Test: Add single number with varargs
     */
    public static void testAddSingleNumber() {
        int result = AdditionProgram.addMultiple(42);
        assertEquals("Add single number (42)", 42, result);
    }

    /**
     * Test: Add no numbers with varargs
     */
    public static void testAddNoNumbers() {
        int result = AdditionProgram.addMultiple();
        assertEquals("Add no numbers (empty)", 0, result);
    }

    /**
     * Test: Add multiple positive doubles
     */
    public static void testAddMultiplePositiveDoubles() {
        double result = AdditionProgram.addMultiple(1.5, 2.5, 3.0, 4.5);
        assertEquals("Add multiple positive doubles (1.5+2.5+3.0+4.5)", 11.5, result);
    }

    /**
     * Test: Add array of positive integers
     */
    public static void testAddArrayPositiveIntegers() {
        int[] array = {10, 20, 30, 40, 50};
        int result = AdditionProgram.addArray(array);
        assertEquals("Add array of positive integers", 150, result);
    }

    /**
     * Test: Add array of mixed integers
     */
    public static void testAddArrayMixedIntegers() {
        int[] array = {100, -50, 75, -25, 50};
        int result = AdditionProgram.addArray(array);
        assertEquals("Add array of mixed integers", 150, result);
    }

    /**
     * Test: Add array of positive doubles
     */
    public static void testAddArrayPositiveDoubles() {
        double[] array = {2.5, 3.5, 4.0, 5.5};
        double result = AdditionProgram.addArray(array);
        assertEquals("Add array of positive doubles", 15.5, result);
    }

    /**
     * Test: Add empty array throws exception
     */
    public static void testAddEmptyArrayThrowsException() {
        try {
            int[] array = {};
            AdditionProgram.addArray(array);
            System.out.println("✗ FAIL: Add empty array should throw exception");
            testsFailed++;
        } catch (IllegalArgumentException e) {
            System.out.println("✓ PASS: Add empty array throws exception");
            testsPassed++;
        }
    }

    /**
     * Test: Add null array throws exception
     */
    public static void testAddNullArrayThrowsException() {
        try {
            AdditionProgram.addArray(null);
            System.out.println("✗ FAIL: Add null array should throw exception");
            testsFailed++;
        } catch (IllegalArgumentException e) {
            System.out.println("✓ PASS: Add null array throws exception");
            testsPassed++;
        }
    }

    /**
     * Test: Calculate average of multiple integers
     */
    public static void testAverageMultipleIntegers() {
        double result = AdditionProgram.average(10, 20, 30, 40);
        assertEquals("Average of multiple integers (10,20,30,40)", 25.0, result);
    }

    /**
     * Test: Calculate average of single integer
     */
    public static void testAverageSingleInteger() {
        double result = AdditionProgram.average(42);
        assertEquals("Average of single integer (42)", 42.0, result);
    }

    /**
     * Test: Calculate average with odd numbers
     */
    public static void testAverageOddNumbers() {
        double result = AdditionProgram.average(5, 10, 15);
        assertEquals("Average of odd numbers (5,10,15)", 10.0, result);
    }

    /**
     * Test: Calculate average of multiple doubles
     */
    public static void testAverageMultipleDoubles() {
        double result = AdditionProgram.average(5.0, 10.0, 15.0, 20.0);
        assertEquals("Average of multiple doubles", 12.5, result);
    }

    /**
     * Test: Average of no numbers throws exception
     */
    public static void testAverageNoNumbersThrowsException() {
        try {
            AdditionProgram.average();
            System.out.println("✗ FAIL: Average of no numbers should throw exception");
            testsFailed++;
        } catch (IllegalArgumentException e) {
            System.out.println("✓ PASS: Average of no numbers throws exception");
            testsPassed++;
        }
    }

    /**
     * Test: Large number addition
     */
    public static void testLargeNumberAddition() {
        int result = AdditionProgram.add(1000000, 2000000);
        assertEquals("Add large numbers (1000000 + 2000000)", 3000000, result);
    }

    /**
     * Test: Precision with decimals
     */
    public static void testDecimalPrecision() {
        double result = AdditionProgram.add(0.1, 0.2);
        assertTrue("Add decimals with precision (0.1 + 0.2)", Math.abs(result - 0.3) < 0.0001);
    }

    /**
     * Main test runner
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   AdditionProgram - Unit Tests v1.0     ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        // Run all tests
        testAddTwoPositiveIntegers();
        testAddTwoNegativeIntegers();
        testAddMixedSignIntegers();
        testAddZero();
        testAddTwoZeros();
        testAddTwoPositiveDoubles();
        testAddTwoNegativeDoubles();
        testAddMultiplePositiveIntegers();
        testAddMultipleMixedIntegers();
        testAddSingleNumber();
        testAddNoNumbers();
        testAddMultiplePositiveDoubles();
        testAddArrayPositiveIntegers();
        testAddArrayMixedIntegers();
        testAddArrayPositiveDoubles();
        testAddEmptyArrayThrowsException();
        testAddNullArrayThrowsException();
        testAverageMultipleIntegers();
        testAverageSingleInteger();
        testAverageOddNumbers();
        testAverageMultipleDoubles();
        testAverageNoNumbersThrowsException();
        testLargeNumberAddition();
        testDecimalPrecision();

        // Print summary
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         Test Results Summary            ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ Tests Passed: " + String.format("%-23d", testsPassed) + "║");
        System.out.println("║ Tests Failed: " + String.format("%-23d", testsFailed) + "║");
        int totalTests = testsPassed + testsFailed;
        System.out.println("║ Total Tests:  " + String.format("%-23d", totalTests) + "║");
        double passRate = totalTests > 0 ? (testsPassed * 100.0 / totalTests) : 0;
        System.out.println("║ Pass Rate:    " + String.format("%-22.1f%%", passRate) + "║");
        System.out.println("╚════════════════════════════════════════╝\n");

        // Exit with appropriate code
        System.exit(testsFailed == 0 ? 0 : 1);
    }
}
