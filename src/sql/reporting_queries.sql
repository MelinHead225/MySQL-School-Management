/* Put your final project reporting queries here */
USE cs_hu_310_final_project;

-- 1. Calculate the GPA for student given a student_id (use student_id=1) 
SELECT students.first_name,
students.last_name,
COUNT(class_registrations.student_id) as number_of_classes,
SUM(class_registrations.grade_id) as total_grade_points_earned,
(SUM(class_registrations.grade_id) / COUNT(class_registrations.student_id)) as GPA
FROM class_registrations
INNER JOIN students ON class_registrations.student_id = students.student_id
WHERE class_registrations.student_id = '1'
GROUP BY class_registrations.student_id;

-- 2. Calculate the GPA for each student (across all classes and all terms)
SELECT students.first_name,
students.last_name,
COUNT(class_registrations.student_id) as number_of_classes,
SUM(class_registrations.grade_id) as total_grade_points_earned,
(SUM(class_registrations.grade_id) / COUNT(class_registrations.student_id)) as GPA
FROM class_registrations
INNER JOIN students ON class_registrations.student_id = students.student_id
GROUP BY class_registrations.student_id;

-- 3. Calculate the avg GPA for each class
SELECT classes.code,
classes.name,
COUNT(class_registrations.class_section_id) AS 'number_of_grades', 
SUM(class_registrations.grade_id) AS 'total_grade_points',
SUM(class_registrations.grade_id) /
COUNT(class_registrations.class_section_id) AS 'AVG GPA'
FROM class_registrations
INNER JOIN class_sections
ON class_sections.class_section_id = class_registrations.class_section_id
INNER JOIN classes
ON class_sections.class_id = classes.class_id
GROUP BY class_registrations.class_section_id;

-- 4. Calculate the avg GPA for each class and term
SELECT classes.code,
classes.name,
terms.name AS 'term', 
COUNT(class_registrations.class_section_id) AS 'number_of_grades', 
SUM(class_registrations.grade_id) AS 'total_grade_points', SUM(class_registrations.grade_id) / 
COUNT(class_registrations.class_section_id) AS 'AVG GPA'
FROM class_registrations
INNER JOIN class_sections
ON class_sections.class_section_id = class_registrations.class_section_id
INNER JOIN classes
ON class_sections.class_id = classes.class_id
INNER JOIN terms
ON terms.term_id = class_sections.term_id
GROUP BY class_sections.term_id, class_sections.class_section_id;

-- 5. List all the classes being taught by an instructor (use instructor_id=1)
SELECT instructors.first_name,
instructors.last_name,
academic_titles.title,
classes.code,
classes.name as class_name,
terms.name   as term
FROM class_sections
INNER JOIN classes
ON class_sections.class_id = classes.class_id
INNER JOIN terms
ON class_sections.term_id = terms.term_id
INNER JOIN instructors
ON instructors.instructor_id = class_sections.instructor_id
INNER JOIN academic_titles
ON instructors.academic_title_id = academic_titles.academic_title_id
WHERE class_sections.instructor_id = 1;

-- 6. List all classes with terms & instructor
SELECT classes.code,
classes.name as name,
terms.name as term, 
instructors.first_name,
instructors.last_name
FROM class_sections
INNER JOIN classes
ON class_sections.class_id = classes.class_id
INNER JOIN terms
ON class_sections.term_id = terms.term_id
INNER JOIN instructors
ON instructors.instructor_id = class_sections.instructor_id
INNER JOIN academic_titles
ON instructors.academic_title_id = academic_titles.academic_title_id;

-- 7. Calculate the remaining space left in a class
SELECT classes.code,
classes.name,
terms.name as term,
(SELECT COUNT(student_id)
FROM class_registrations inner_class_registrations
WHERE inner_class_registrations.class_section_id = outer_class_registrations.class_section_id
GROUP BY class_section_id) as enrolled_students, classes.maximum_students - (SELECT COUNT(student_id)
FROM class_registrations inner_class_registrations
WHERE inner_class_registrations.class_section_id = outer_class_registrations.class_section_id
GROUP BY class_section_id) as space_remaining
FROM class_registrations outer_class_registrations
INNER JOIN class_sections
ON class_sections.class_section_id = outer_class_registrations.class_section_id
INNER JOIN classes
ON class_sections.class_id = classes.class_id
INNER JOIN terms
ON terms.term_id = class_sections.term_id
GROUP BY outer_class_registrations.class_section_id;


