/**
 * AdditionProgram - A comprehensive Java addition utility
 * 
 * This program provides multiple ways to perform addition operations:
 * 1. Simple two-number addition
 * 2. Multiple number addition using varargs
 * 3. Array-based addition
 * 4. Interactive command-line addition
 * 5. Arithmetic operations with validation
 * 
 * @author Bold Create Agent
 * @version 1.0
 */

import java.util.Scanner;
import java.util.InputMismatchException;

public class AdditionProgram {

    /**
     * Add two integers
     * 
     * @param a First number
     * @param b Second number
     * @return Sum of a and b
     */
    public static int add(int a, int b) {
        return a + b;
    }

    /**
     * Add two double numbers
     * 
     * @param a First number
     * @param b Second number
     * @return Sum of a and b
     */
    public static double add(double a, double b) {
        return a + b;
    }

    /**
     * Add multiple integers using varargs
     * 
     * @param numbers Variable number of integers to add
     * @return Sum of all numbers
     */
    public static int addMultiple(int... numbers) {
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        return sum;
    }

    /**
     * Add multiple double numbers using varargs
     * 
     * @param numbers Variable number of doubles to add
     * @return Sum of all numbers
     */
    public static double addMultiple(double... numbers) {
        double sum = 0.0;
        for (double num : numbers) {
            sum += num;
        }
        return sum;
    }

    /**
     * Add all numbers in an array
     * 
     * @param numbers Array of integers
     * @return Sum of all array elements
     * @throws IllegalArgumentException if array is empty
     */
    public static int addArray(int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        return sum;
    }

    /**
     * Add all numbers in a double array
     * 
     * @param numbers Array of doubles
     * @return Sum of all array elements
     * @throws IllegalArgumentException if array is empty
     */
    public static double addArray(double[] numbers) {
        if (numbers == null || numbers.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }
        double sum = 0.0;
        for (double num : numbers) {
            sum += num;
        }
        return sum;
    }

    /**
     * Calculate average of multiple numbers
     * 
     * @param numbers Variable number of integers
     * @return Average of all numbers
     * @throws IllegalArgumentException if no numbers provided
     */
    public static double average(int... numbers) {
        if (numbers.length == 0) {
            throw new IllegalArgumentException("At least one number required for average");
        }
        return (double) addMultiple(numbers) / numbers.length;
    }

    /**
     * Calculate average of multiple double numbers
     * 
     * @param numbers Variable number of doubles
     * @return Average of all numbers
     * @throws IllegalArgumentException if no numbers provided
     */
    public static double average(double... numbers) {
        if (numbers.length == 0) {
            throw new IllegalArgumentException("At least one number required for average");
        }
        return addMultiple(numbers) / numbers.length;
    }

    /**
     * Interactive mode - Allows user to input numbers and add them
     */
    public static void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== Addition Program - Interactive Mode ===\n");

        try {
            System.out.println("How many numbers do you want to add?");
            int count = scanner.nextInt();

            if (count <= 0) {
                System.out.println("Error: Please enter a positive number of values.");
                return;
            }

            double[] numbers = new double[count];
            System.out.println("\nEnter " + count + " numbers (integers or decimals):");

            for (int i = 0; i < count; i++) {
                System.out.print("Number " + (i + 1) + ": ");
                numbers[i] = scanner.nextDouble();
            }

            double sum = addArray(numbers);
            double avg = average(numbers);

            System.out.println("\n--- Results ---");
            System.out.println("Sum: " + sum);
            System.out.println("Average: " + String.format("%.2f", avg));
            System.out.println("Count: " + count);

        } catch (InputMismatchException e) {
            System.out.println("Error: Please enter valid numbers.");
            scanner.nextLine(); // Clear invalid input
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    /**
     * Demonstration of all addition methods
     */
    public static void demonstrationMode() {
        System.out.println("\n=== Addition Program - Demonstration Mode ===\n");

        // Two-number addition
        System.out.println("1. Two Number Addition:");
        int result1 = add(5, 3);
        System.out.println("   add(5, 3) = " + result1);

        double result2 = add(5.5, 3.2);
        System.out.println("   add(5.5, 3.2) = " + result2);

        // Multiple number addition
        System.out.println("\n2. Multiple Number Addition (varargs):");
        int result3 = addMultiple(1, 2, 3, 4, 5);
        System.out.println("   addMultiple(1, 2, 3, 4, 5) = " + result3);

        double result4 = addMultiple(1.5, 2.5, 3.0, 4.5);
        System.out.println("   addMultiple(1.5, 2.5, 3.0, 4.5) = " + result4);

        // Array addition
        System.out.println("\n3. Array Addition:");
        int[] intArray = {10, 20, 30, 40, 50};
        int result5 = addArray(intArray);
        System.out.println("   addArray([10, 20, 30, 40, 50]) = " + result5);

        double[] doubleArray = {2.5, 3.5, 4.0, 5.5};
        double result6 = addArray(doubleArray);
        System.out.println("   addArray([2.5, 3.5, 4.0, 5.5]) = " + result6);

        // Average calculation
        System.out.println("\n4. Average Calculation:");
        double result7 = average(10, 20, 30, 40);
        System.out.println("   average(10, 20, 30, 40) = " + String.format("%.2f", result7));

        double result8 = average(5.5, 6.5, 7.5, 8.5);
        System.out.println("   average(5.5, 6.5, 7.5, 8.5) = " + String.format("%.2f", result8));

        // Complex example
        System.out.println("\n5. Complex Addition Example:");
        int complexResult = addMultiple(100, 50, 75, 25, 150, 200);
        System.out.println("   addMultiple(100, 50, 75, 25, 150, 200) = " + complexResult);
    }

    /**
     * Main method - Entry point of the program
     * 
     * @param args Command line arguments
     *             "demo" - Run demonstration mode
     *             "interactive" - Run interactive mode
     *             (no args or invalid args) - Display menu
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     Welcome to Addition Program v1.0    ║");
        System.out.println("║  A Comprehensive Java Addition Utility  ║");
        System.out.println("╚════════════════════════════════════════╝");

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("demo")) {
                demonstrationMode();
            } else if (args[0].equalsIgnoreCase("interactive")) {
                interactiveMode();
            } else {
                System.out.println("\nUnknown argument: " + args[0]);
                showMenu();
            }
        } else {
            showMenu();
        }

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     Thank you for using Addition        ║");
        System.out.println("║              Program v1.0               ║");
        System.out.println("╚════════════════════════════════════════╝\n");
    }

    /**
     * Display interactive menu
     */
    private static void showMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Interactive Mode (Add your own numbers)");
            System.out.println("2. Demonstration Mode (View examples)");
            System.out.println("3. Quick Add Two Numbers");
            System.out.println("4. Exit");
            System.out.print("\nSelect an option (1-4): ");

            try {
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        interactiveMode();
                        break;
                    case 2:
                        demonstrationMode();
                        break;
                    case 3:
                        quickAdd(scanner);
                        break;
                    case 4:
                        System.out.println("\nExiting program...");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please select 1-4.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }

        scanner.close();
    }

    /**
     * Quick add - Simple addition of two numbers
     * 
     * @param scanner Scanner object for user input
     */
    private static void quickAdd(Scanner scanner) {
        System.out.println("\n=== Quick Add Two Numbers ===");
        try {
            System.out.print("Enter first number: ");
            double num1 = scanner.nextDouble();
            System.out.print("Enter second number: ");
            double num2 = scanner.nextDouble();

            double result = add(num1, num2);
            System.out.println("\nResult: " + num1 + " + " + num2 + " = " + result);
        } catch (InputMismatchException e) {
            System.out.println("Error: Please enter valid numbers.");
            scanner.nextLine();
        }
    }
}
