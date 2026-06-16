-- create Employee(key, name, dept, age)
CREATE TABLE Department(
    key SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
--creat Department(key, name) Table
CREATE TABLE Employee (
                          key     SERIAL        PRIMARY KEY,
                          name    VARCHAR(100)  NOT NULL,
                          dept    INT           REFERENCES Department(key),
                          age     INT,
                          salary  NUMERIC(10,2)
);
--insert some dummy data
INSERT INTO Department (name) VALUES
                                  ('Engineering'),
                                  ('Marketing'),
                                  ('Finance');

INSERT INTO Employee (name, dept, age, salary) VALUES
                                                   ('Alice',  1, 29, 95000),
                                                   ('Bob',    1, 35, 78000),
                                                   ('Carol',  2, 42, 62000),
                                                   ('David',  2, 31, 85000),
                                                   ('Eve',    3, 27, 110000),
                                                   ('Frank',  3, 50, 73000),
                                                   ('Grace',  1, 38, 91000);
-- Get only employee names and ages.
SELECT name, age FROM Employee;
--Find employees older than 30.
SELECT * FROM Employee WHERE age > 30;
-- Find employees whose salary is greater than 80,000.
SELECT * FROM Employee WHERE salary > 80000;
--List employees ordered by age (ascending).
SELECT * FROM Employee ORDER BY age ASC;
--Get the top 3 highest-paid employees.
SELECT * FROM Employee ORDER BY salary DESC LIMIT 3;
--Count total number of employees.
SELECT COUNT(*) AS total_employees FROM Employee;
--Find the average salary of all employees.
SELECT ROUND(AVG(salary), 2) AS avg_salary FROM Employee;
--List employee name with department name.
SELECT e.name AS employee, d.name AS department FROM Employee e
         JOIN Department d ON e.dept = d.key;
--Find employees earning the highest salary.
SELECT * FROM Employee
WHERE salary = (SELECT MAX(salary) FROM Employee);
--Find employees earning the second highest salary.
SELECT *
FROM Employee
WHERE salary = (
    SELECT DISTINCT salary
    FROM Employee
    ORDER BY salary DESC
    LIMIT 1 OFFSET 1
);
-- Find employees earning the third highest salary.
SELECT *
FROM Employee
WHERE salary = (
    SELECT DISTINCT salary
    FROM Employee
    ORDER BY salary DESC
    LIMIT 1 OFFSET 2
);