-- Insert sample companies
INSERT INTO companies (id, name, industry, address, website) VALUES
(1, 'TechSoft', 'Technology', '123 Tech Road', 'https://techsoft.com'),
(2, 'HealthPlus', 'Healthcare', '456 Health St', 'https://healthplus.com'),
(3, 'EduWorld', 'Education', '789 Edu Ave', 'https://eduworld.com');

-- Insert sample employees
INSERT INTO employees (id, first_name, last_name, email, date_of_birth, position, salary, company_id) VALUES
(1, 'Alice', 'Johnson', 'alice.johnson@example.com', '1985-07-10', 'Developer', 80000, 1),
(2, 'Bob', 'Smith', 'bob.smith@example.com', '1990-05-23', 'Manager', 95000, 1),
(3, 'Carol', 'Davis', 'carol.davis@example.com', '1988-11-15', 'Analyst', 70000, 2),
(4, 'David', 'Brown', 'david.brown@example.com', '1992-02-28', 'Developer', 78000, 2),
(5, 'Eva', 'Wilson', 'eva.wilson@example.com', '1987-09-04', 'Teacher', 60000, 3),
(6, 'Frank', 'Taylor', 'frank.taylor@example.com', '1983-01-30', 'Principal', 85000, 3);