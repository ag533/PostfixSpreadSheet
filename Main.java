import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class Main {

    private static final int VISITED = 1;
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java com.example.Main $INPUT_FILE");
            return;
        }

        String inputFile = args[0];

        try {
            String[][] expressions = parseCSV(inputFile);
            if (expressions.length == 0) {
                System.out.println("Empty File");
                return;
            }
            double[][] computedValues = evaluateExpressions(expressions);
            printOutput(computedValues);
        } catch (IOException e) {
            System.out.println("Error reading the input file: " + e.getMessage());
        }
    }

    private static String[][] parseCSV(String inputFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            int rowCount = 0;
            int colCount = 0;
    
            // Count the number of rows and columns in the CSV file
            while ((line = reader.readLine()) != null) {
                rowCount++;
                String[] cells = line.split(",", -1); // Set limit to -1 to keep trailing empty cells
                colCount = Math.max(colCount, cells.length);
            }
    
            String[][] expressions = new String[rowCount][colCount];
    
            // If the CSV file is empty, return the empty expressions array
            if (rowCount == 0) {
                return expressions;
            }
    
            // Reset the reader to start reading from the beginning of the file
            reader.close();
    
            // Open the file again to read the expressions
            try (BufferedReader newReader = new BufferedReader(new FileReader(inputFile))) {
                int row = 0;
                while ((line = newReader.readLine()) != null) {
                    String[] cells = line.split(",", -1);
                    for (int col = 0; col < cells.length; col++) {
                        expressions[row][col] = cells[col].trim();
                    }
                    row++;
                }
            }
    
            return expressions;
        }
    }

    private static double[][] evaluateExpressions(String[][] expressions) {
        int rowCount = expressions.length;
        int colCount = expressions[0].length;
        double[][] computedValues = new double[rowCount][colCount];
        int[][] isComputed = new int[rowCount][colCount];

        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                String cellExpression = expressions[row][col];
                try {
                    computedValues[row][col] = evaluatePostfixExpression(cellExpression, computedValues, isComputed, expressions, row, col);
                    isComputed[row][col] = VISITED;
                } catch (IllegalArgumentException e) {
                    computedValues[row][col] = Double.NaN;
                }
            }
        }

        return computedValues;
    }

    private static double evaluatePostfixExpression(String expression, double[][] computedValues, int[][] isComputed, String[][] expressions, int row, int col) {
        String[] tokens = expression.split("\\s+");
        Deque<Double> stack = new ArrayDeque<>();

        for (String token : tokens) {
            if (isOperator(token)) {
                // Ensure that there are enough operands on the stack to perform the operation
                int operandsRequired = getOperandsRequired(token);
                if (stack.size() < operandsRequired) {
                    throw new IllegalArgumentException("Insufficient operands for operator: " + token);
                }
    
                // Pop the operands from the stack in reverse order and perform the operation
                double[] operands = new double[operandsRequired];
                for (int i = operandsRequired - 1; i >= 0; i--) {
                    operands[i] = stack.pop();
                }
    
                double result = performOperation(token, operands);
                stack.push(result);
            } else if (isCellReference(token)) {
                int referencedRow = getRowIndex(token, row);
                int referencedCol = getColIndex(token, col);
                if(isComputed[referencedRow][referencedCol] == 0) {
                    if (referencedRow == row && referencedCol == col) {
                        throw new IllegalArgumentException("Self calling cell: " + token);
                    }
                    computedValues[referencedRow][referencedCol] = evaluatePostfixExpression(expressions[referencedRow][referencedCol], computedValues, isComputed, expressions, referencedRow, referencedCol);
                    isComputed[referencedRow][referencedCol] = VISITED;
                }
                stack.push(computedValues[referencedRow][referencedCol]);
            } else {
                double number = Double.parseDouble(token);
                stack.push(number);
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid expression: " + expression);
        }
        return stack.pop();
    }

    private static int getOperandsRequired(String operator) {
        // Return the number of operands required for the given operator
        switch (operator) {
            case "+":
            case "-":
            case "*":
            case "/":
                return 2;
            // Add more cases here for other operators if needed
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

    private static boolean isOperator(String token) {
        return token.matches("[+\\-*/]");
    }

    private static boolean isCellReference(String token) {
        return token.matches("[A-Za-z]\\d+");
    }

    private static double performOperation(String operator, double[] operands) {
        // Perform the operation based on the operator and operands
        switch (operator) {
            case "+":
                return operands[0] + operands[1];
            case "-":
                return operands[0] - operands[1];
            case "*":
                return operands[0] * operands[1];
            case "/":
                if (operands[1] == 0) {
                    throw new IllegalArgumentException("Division by zero");
                }
                return operands[0] / operands[1];
            // Add more cases here for other operators if needed
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

    private static int getColIndex(String cellReference, int currentCol) {
        int col = currentCol;
        char colChar = cellReference.charAt(0);
    
        if (Character.isUpperCase(colChar)) {
            col = colChar - 'A';
        } else if (Character.isLowerCase(colChar)) {
            col = colChar - 'a';
        } else {
            throw new IllegalArgumentException("Invalid cell reference: " + cellReference);
        }
    
        return col;
    }

    private static int getRowIndex(String cellReference, int currentRow) {
        int row = currentRow;
        try {
            row = Integer.parseInt(cellReference.substring(1)) - 1;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid column index: " + cellReference);
        }
        return row;
    }

    private static void printOutput(double[][] computedValues) {
        for (int row = 0; row < computedValues.length; row++) {
            for (int col = 0; col < computedValues[row].length; col++) {
                double cellValue = computedValues[row][col];
                if (Double.isNaN(cellValue)) {
                    System.out.print("#ERR,");
                } else {
                    System.out.print(cellValue + ",");
                }
            }
            System.out.println();
        }
    }
}
