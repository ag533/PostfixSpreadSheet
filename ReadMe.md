## Code structure

1. To keep the code structure simple and redable I have divided the code into multiple function.
   I did this to make the code more redable. Also if we want to extend this project
   these function can go into the helper classes and can be used through out the
   project which we save from replicating the helper functions again and again.

2. Defining saperate funcitions inside the code.

   #### main function

   This function is dealing with reading arguments from the commandline and throwing error if there was some issue reading the file.

   #### parseCSV

   This function is used to parse the CSV file and store the data in a 2d string array of expressions.

   #### evaluateExpressions

   This function is used to evaluate the expression that are inside the 2d string array created by parseCSV to evaluate the postfix expression it calls evaluatePostfixExpression function.

   #### evaluatePostfixExpression

   This function is used to evaluate the expression based on the rules defined on the postfix wiki page. This function also deals with cell reference recursively. It deals with all edge cases of an invalid expression.

   #### getOperandsRequired

   Helper function to see how many operands are required for a particular operator if not met will throw the error.

   #### isOperator

   helper function to check if the token is operator.

   #### isCellReference

   helper function to check if the token is cellRef.

   #### performOperation

   function to perform the arithmetic operation.

   #### getColIndex

   function to get the column of the referenced element.

   #### getRowIndex

   function to get the row of the referenced element.

   #### printOutput

   function to print the output.

3. Most of the code is delf defined and easy to read and I have used appropriate
   names related to the functioanlity of the function or values stored in the variable.

## Future improvements that can be done on this.

1. The performance of the project can be increased by using multithreading instead
   of single thread implementation as done in the project.
2. Caching can also be implemented to save time if a file is already evaluated.

## Test cases.

I have tested the project on the following cases:

1. Long postfix expressions.
2. Deeply nested ref calls.
3. Empty file.
4. CSV file with empty records.
5. Given test case in the problem statement.
6. Wrong expressions.
7. Self calling cells.
8. Very huge csv file.

I can't think of any other edge cases but feel free to test with your own test
suite I am very hopeful that most/all of the test cases will pass.
