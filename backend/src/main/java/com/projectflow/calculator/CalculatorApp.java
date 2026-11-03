package com.projectflow.calculator;

import java.util.Scanner;

/**
 * Interactive Calculator Application with command-line interface.
 */
public class CalculatorApp {
    private static final Calculator calculator = new Calculator();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     Welcome to Java Calculator         ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        performAddition(scanner);
                        break;
                    case "2":
                        performSubtraction(scanner);
                        break;
                    case "3":
                        performMultiplication(scanner);
                        break;
                    case "4":
                        performDivision(scanner);
                        break;
                    case "5":
                        performPower(scanner);
                        break;
                    case "6":
                        performSquareRoot(scanner);
                        break;
                    case "7":
                        performAbsoluteValue(scanner);
                        break;
                    case "8":
                        System.out.println("\nThank you for using the Calculator. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("❌ Invalid option. Please try again.\n");
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage() + "\n");
            }
        }

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("Select an operation:");
        System.out.println("1. Addition (+)");
        System.out.println("2. Subtraction (-)");
        System.out.println("3. Multiplication (*)");
        System.out.println("4. Division (/)");
        System.out.println("5. Power (^)");
        System.out.println("6. Square Root (√)");
        System.out.println("7. Absolute Value (|x|)");
        System.out.println("8. Exit");
        System.out.print("Enter your choice (1-8): ");
    }

    private static void performAddition(Scanner scanner) {
        double a = getNumberInput(scanner, "Enter first number: ");
        double b = getNumberInput(scanner, "Enter second number: ");
        double result = calculator.add(a, b);
        displayResult(a, b, "+", result);
    }

    private static void performSubtraction(Scanner scanner) {
        double a = getNumberInput(scanner, "Enter first number: ");
        double b = getNumberInput(scanner, "Enter second number: ");
        double result = calculator.subtract(a, b);
        displayResult(a, b, "-", result);
    }

    private static void performMultiplication(Scanner scanner) {
        double a = getNumberInput(scanner, "Enter first number: ");
        double b = getNumberInput(scanner, "Enter second number: ");
        double result = calculator.multiply(a, b);
        displayResult(a, b, "*", result);
    }

    private static void performDivision(Scanner scanner) {
        double a = getNumberInput(scanner, "Enter dividend: ");
        double b = getNumberInput(scanner, "Enter divisor: ");
        double result = calculator.divide(a, b);
        displayResult(a, b, "/", result);
    }

    private static void performPower(Scanner scanner) {
        double base = getNumberInput(scanner, "Enter base: ");
        double exponent = getNumberInput(scanner, "Enter exponent: ");
        double result = calculator.power(base, exponent);
        System.out.printf("✓ Result: %.0f ^ %.0f = %.2f\n\n", base, exponent, result);
    }

    private static void performSquareRoot(Scanner scanner) {
        double number = getNumberInput(scanner, "Enter number: ");
        double result = calculator.squareRoot(number);
        System.out.printf("✓ Result: √%.2f = %.2f\n\n", number, result);
    }

    private static void performAbsoluteValue(Scanner scanner) {
        double number = getNumberInput(scanner, "Enter number: ");
        double result = calculator.absoluteValue(number);
        System.out.printf("✓ Result: |%.2f| = %.2f\n\n", number, result);
    }

    private static double getNumberInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input. Please enter a valid number.");
        }
    }

    private static void displayResult(double a, double b, String operator, double result) {
        System.out.printf("✓ Result: %.2f %s %.2f = %.2f\n\n", a, operator, b, result);
    }
}
