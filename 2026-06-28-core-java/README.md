Core Java Lessons - Modular examples aimed at junior engineers

This project contains lesson modules for core Java topics:
- Data Types
- Loops & Conditionals
- Object-Oriented Programming (OOP)
- Exception Handling
- Collections Framework
- Java 8+ Features (Streams, Lambdas)

How to use
1. Open the project in IntelliJ/Eclipse or use command line with Maven.
2. Compile: mvn -q -DskipTests package
3. Run examples: mvn -q exec:java -Dexec.mainClass="com.you.corejava.datatype.DataTypesLesson" (or run via IDE)

Structure
- src/main/java/com/you/corejava/<module> contains lesson classes with examples and explanations as comments.

Best practices
Read code and comments, run the examples, then modify them. Each lesson favors immutability, small methods, clear naming, and returning empty collections rather than null.

Next steps
- Run individual lessons and unit tests under src/test.
- Convert lessons into exercises by writing additional tests.
