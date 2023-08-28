import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
 
public class FinalProject {
  public static void main (String[] args) {

    Scanner sc = new Scanner(System.in);
    Scanner fi = null;
    ArrayList<String> courses = new ArrayList<String>();
    String filePath; 

    try {

      fi = new Scanner(new File("lec.txt"));

    } catch (Exception e) {

      System.out.println(e.getMessage());

    }

    while (fi.hasNextLine()) {

      courses.add(fi.nextLine());

    }

    fi.close();

    while (true) {

      System.out.print("Enter the absolute path of the file: ");
      filePath = sc.nextLine();
      File file = new File(filePath); 

      if (file.exists() && file.isFile()) {

        System.out.println("File found! Let's proceed...");
        break;

      } else if (!file.isFile()) {

        System.out.println("Sorry no such file.");

      }
    }

    ArrayList<Person> list = new ArrayList<Person>();
    int choice = 0;

    System.out.println("*****************************************");

    while (choice != 7) {
      System.out.println("Choose one of these options:");
      System.out.println("1- Add a new Faculty to the schedule");
      System.out.println("2- Enroll a student to a Lecture");
      System.out.println("3- Print the schedule of a Faculty");
      System.out.println("4- Print the schedule of an TA");
      System.out.println("5- Print the schedule of a Student");
      System.out.println("6- Delete a Lecture");
      System.out.println("7- Exit");

      while (true) {

        System.out.print("Enter your choice: ");

        try {

          choice = Integer.parseInt(sc.nextLine());

          if (choice < 1 || choice > 7) {

            System.out.println("Please enter a number between 1 and 7.");

          } else {

            break;

          }
        } catch (NumberFormatException e) {

          System.out.println("Please enter an integer.");
          
        }
      }


      switch (choice) {
        case 1: {

          String facultyId;

          System.out.print("Enter UCF id: ");
          facultyId = sc.nextLine();

          while (facultyId.length() != 7 || !facultyId.matches("[0-9]+")) {

            try {

              throw new IdException();

            } catch (IdException e) {

              System.out.println(e.getMessage());
              System.out.print("Enter UCF id (7 digits): ");
              facultyId = sc.nextLine();

            }
          }

          boolean facultyTaken = false;

          for (Person p : list) {

            if (p.getId().equals(facultyId)) {

              facultyTaken = true;
              break;

            }

          }

          if (facultyTaken) {

            System.out.println("This UCF id is already in use");
            break;

          }

          String facultyName;
          String facultyRank;
          String[] lecturesCrns;

          System.out.print("Enter name: ");
          facultyName = sc.nextLine();
          System.out.print("Enter rank: ");
          facultyRank = sc.nextLine();

          while (!facultyRank.equalsIgnoreCase("professor") && !facultyRank.equalsIgnoreCase("associate professor") && !facultyRank.equalsIgnoreCase("assistant professor") && !facultyRank.equalsIgnoreCase("adjunct")) {

            System.out.print("Enter a valid rank: ");
            facultyRank = sc.nextLine();

          }

          System.out.print("Enter office location: ");
          sc.nextLine();

          System.out.print("Enter how many lectures: ");

          boolean correctInt = false; 
        
          while(!correctInt) {

            if(sc.hasNextInt()) {

              correctInt = true; 
              sc.nextLine();

            } else {

              System.out.print("Hey that is not a number! ");
              sc.nextLine();
                  
            }
          }

          boolean lecturesTaken = false;
          
          do  {

            lecturesTaken = false;
            
            System.out.print("Enter the crns of the lectures to assign to this faculty: ");
            lecturesCrns = sc.nextLine().split(" ");

            for (Person p : list) {

              if (p instanceof Faculty) {

                ArrayList<String> takenLectures = ((Faculty)p).getLectures();

                for (String currentLecture : takenLectures) {

                  for (String lecture : lecturesCrns) {

                    if (currentLecture.equals(lecture)) {

                      System.out.println("Crn " + lecture + " is already being taught");
                      lecturesTaken = true;
                    }
                  }
                }
              }
            }
          } while (lecturesTaken);

          Person newF = new Faculty(facultyName, facultyId, facultyRank);
          
          for (String lectureCrn : lecturesCrns) {

            ((Faculty)newF).addLecture(lectureCrn);

          }

          list.add(newF);

          for (String lecture : lecturesCrns) {

            for (int i = 0; i < courses.size(); i++) {

              String[] courseArr = courses.get(i).split(",");
              
              if (courseArr[0].equals(lecture) && courses.get(i + 1).length() < 15) {

                System.out.println("[" + courseArr[0] + "/" + courseArr[1] + "/" + courseArr[2] + "]" + " has these labs:");

                System.out.println("\t" + courses.get(i + 1));
                System.out.println("\t" + courses.get(i + 2));
                System.out.println("\t" + courses.get(i + 3));

                for (int j = 1; j < 4; j++) {

                  String lab = courses.get(i + j).substring(0, 5);

                  String taId;

                  boolean taIdUnavailable = false;
                  
                  do {
                    
                    boolean taTaken = false;
                    taIdUnavailable = false;

                    System.out.print("Enter the TA's id for " + lab + ": ");
                    taId = sc.nextLine();

                    while (taId.length() != 7  || !taId.matches("[0-9]+")) {

                      try {

                        throw new IdException();
                        
                      } catch (IdException e) {

                        System.out.println(e.getMessage());
                        System.out.print("Enter TA's id for " + lab + " (7 digits): ");
                        taId = sc.nextLine();

                      }
                    }
                  
                    for (Person p : list) {

                      if (p.getId().equals(taId)) {

                        if (p instanceof Faculty) {

                          System.out.println("This UCF id is already in use by a faculty");
                          taIdUnavailable = true;
                          taTaken = true;
                          break;

                        } else if (p instanceof Ta) {
                          
                          taTaken = true;

                          ArrayList<Course> coursesEnrolled = ((Ta)p).getCourses();

                          for (Course courseEnrolled : coursesEnrolled) {

                            if (courseEnrolled.getTitle().equals(lecture)) {

                              taIdUnavailable = true;
                              System.out.println(p.getName() + " is enrolled in this lecture");
                              break;

                            }

                          }

                          if (!taIdUnavailable) {

                            ((Ta)p).addLab(lab);
                            break;

                          }

                        } else {
                          
                          taTaken = true;

                          ArrayList<Course> coursesEnrolled = ((Student)p).getCourses();

                          for (Course courseEnrolled : coursesEnrolled) {

                            if (courseEnrolled.getTitle().equals(lecture)) {

                              taIdUnavailable = true;
                              System.out.println(p.getName() + " is enrolled in this lecture");
                              break;

                            }
                          }
                        }

                        if (!taIdUnavailable) {

                          String taSupervisor;
                          boolean supervisorExists = false;
                          String taDegree;
                          
                          System.out.println("TA found as student: " + p.getName());

                          do {

                            supervisorExists = false;

                            System.out.print("TA's supervisor name: ");
                            taSupervisor = sc.nextLine();

                            for (Person q : list) {
                              
                              if (q instanceof Faculty) {
                                
                                if (taSupervisor.equalsIgnoreCase(q.getName())) {

                                  supervisorExists = true;
                                  break;
                                  
                                }
                              }
                            }

                            if (!supervisorExists) {

                              System.out.println(taSupervisor + " is not a faculty member");

                            }
                          } while (!supervisorExists);

                          System.out.print("Degree Seeking: ");
                          taDegree = sc.nextLine();
                          
                          while(!taDegree.equalsIgnoreCase("ms") && !taDegree.equalsIgnoreCase("phd")) {

                            System.out.print("Enter a valid degree (MS or PhD): ");
                            taDegree = sc.nextLine();
                            
                          }
                          
                          ArrayList<Course> studentCourses = ((Student)p).getCourses();
                          
                          p = new Ta(p.getName(), taId, "graduate", taDegree, taSupervisor);
                          
                          ((Ta)p).addLab(lab);
                          
                          for (Course studentCourse : studentCourses) {
                            
                            ((Ta)p).addCourse(studentCourse);

                          }
                        
                          break;
                            
                        }
                      }
                    }

                    if (!taTaken) {

                      String taName;
                      String taSupervisor;
                      String taDegree;

                      System.out.print("Name of TA: ");
                      taName = sc.nextLine();

                      boolean supervisorExists = false;

                      do {

                        supervisorExists = false;

                        System.out.print("TA's supervisor name: ");
                        taSupervisor = sc.nextLine();

                        for (Person q : list) {
                          
                          if (q instanceof Faculty) {
                            
                            if (taSupervisor.equalsIgnoreCase(q.getName())) {

                              supervisorExists = true;
                              break;
                              
                            }
                          }
                        }

                        if (!supervisorExists) {

                          System.out.println(taSupervisor + " is not a faculty member");

                        }
                      } while (!supervisorExists);

                      System.out.print("Degree Seeking: ");
                      taDegree = sc.nextLine();

                      while(!taDegree.equalsIgnoreCase("ms") && !taDegree.equalsIgnoreCase("phd")) {

                        System.out.print("Enter a valid degree (MS or PhD): ");
                        taDegree = sc.nextLine();
                        
                      }

                      Person newTa = new Ta(taName, taId, "graduate", taDegree, taSupervisor);
                      ((Ta)newTa).addLab(lab);
                      list.add(newTa);

                    }

                  } while (taIdUnavailable);

                }

                System.out.println("[" + courseArr[0] + "/" + courseArr[1] + "/" + courseArr[2] + "]" + " Added!");
                break;

              }
              else if(courseArr[0].equals(lecture) && courses.get(i + 1).length() > 15) {

                System.out.println("[" + courseArr[0] + "/" + courseArr[1] + "/" + courseArr[2] + "]" + " Added!"); 
                
              }
            }
          }
          
          break;
        } case 2: {

          String studentId;
          boolean studentIdUnavailable = false;
          String studentName = "";
          String studentType;
          String studentLecture = "";
          Person newP = new Student();

          do {

            studentIdUnavailable = false;

            System.out.print("Enter UCF id: ");
            studentId = sc.nextLine();

            while (studentId.length() != 7 || !studentId.matches("[0-9]+")) {

              try {

                throw new IdException();

              } catch (IdException e) {

                System.out.println(e.getMessage());
                System.out.print("Enter UCF id (7 digits): ");
                studentId = sc.nextLine();

              }
            }

            boolean studentTaken = false;

            for (Person p : list) {

              if (p.getId().equals(studentId)) {

                if (p instanceof Faculty) {

                  System.out.println("This UCF id is already in use by a faculty");
                  studentIdUnavailable = true;

                } else if (p instanceof Ta) {

                  newP = (Ta)p;
                  
                } else if (p instanceof Student) {

                  newP = (Student)p;
                  
                }

                studentTaken = true;

                if (!studentIdUnavailable) {

                  studentName = p.getName();

                  System.out.println("Record found/Name: " + studentName);
                  System.out.print("Which lecture to enroll [" + studentName + "] in? ");
                  studentLecture = sc.nextLine();

                }
              } 
            }

            if (!studentTaken) {

              System.out.print("Enter name: ");
              studentName = sc.nextLine();
              System.out.print("Enter type: ");
              studentType = sc.nextLine();
              System.out.print("Enter the crn of the lecture: ");
              studentLecture = sc.nextLine();

              newP = new Student(studentName, studentId, studentType);
              list.add(newP);

            }

          } while (studentIdUnavailable);

          boolean isEnrolled = false;

          do {

            isEnrolled = false;

            ArrayList<Course> studentCourses = ((Student)newP).getCourses();

            for (Course enrolledCourse : studentCourses) {

              if (enrolledCourse.getCrn().equals(studentLecture)) {

                System.out.print("Student is already enrolled in lecture, enter another crn: ");
                studentLecture = sc.nextLine();

                isEnrolled = true;

                break;

              }
            }
          } while (isEnrolled);

          boolean isTeachingLab = false;

          do {

            isTeachingLab = false;

            if (newP instanceof Ta) {

              ArrayList<String> taLabs = ((Ta)newP).getLabs();

              for (int i = 0; i < courses.size(); i++) {

                String[] courseArr = courses.get(i).split(",");

                if (courseArr[0].equals(studentLecture)) {

                  for (String taLab : taLabs) {

                    if (taLab.equals(courses.get(i + 1).substring(0, 5)) || taLab.equals(courses.get(i + 2).substring(0, 5)) || taLab.equals(courses.get(i + 3).substring(0, 5))) {

                      isTeachingLab = true;
                      System.out.print("Ta is teaching lab for this lecture, enter another crn: ");
                      studentLecture = sc.nextLine();
                      
                      break;

                    }
                

                  }

                  break;
                }
              }              
            }
          } while (isTeachingLab);

          for (int i = 0; i < courses.size(); i++) {

            String[] courseArr = courses.get(i).split(",");

            if (courseArr[0].equals(studentLecture) && courses.get(i + 1).length() < 15) {

              String lab;

              System.out.println("[" + courseArr[1] + "/" + courseArr[2] + "]" + " has these labs: ");

              System.out.println("\t" + courses.get(i + 1));
              System.out.println("\t" + courses.get(i + 2));
              System.out.println("\t" + courses.get(i + 3));

              lab = courses.get(i + (int)(Math.random() * 3) + 1).substring(0, 5);

              System.out.println("[" + studentName + "] is added to lab: " + lab);

              Course newC = new Course(courseArr[0], courseArr[1], courseArr[2], lab);

              ((Student)newP).addCourse(newC);

            } else if(courseArr[0].equals(studentLecture)) { 

              System.out.println("Sorry no lab exists for this lecture");
              Course newC = new Course(courseArr[0], courseArr[1], courseArr[2]);

              ((Student)newP).addCourse(newC);
            }
          }

          System.out.println("Student Enrolled!");

          break;
        } case 3: {

          String facultyId;

          System.out.print("Enter the UCF id: ");
          facultyId = sc.nextLine();

          while (facultyId.length() != 7 || !facultyId.matches("[0-9]+")) {

          try {

              throw new IdException();

            } catch (IdException e) {

              System.out.println(e.getMessage());
              System.out.print("Enter UCF id (7 digits): ");
              facultyId = sc.nextLine();

            }
          }  

          boolean idExists = false;

          for (Person p : list) {

            if (p.getId().equals(facultyId)) {

              if (p instanceof Faculty) {

                System.out.println(p.getName() + " is teaching the following lectures:");

                ArrayList<String> facultyCrns = ((Faculty)p).getLectures();

                for (String facultyCrn : facultyCrns) {

                  for (int i = 0; i < courses.size(); i++) {
                    
                    if (facultyCrn.equals(courses.get(i).substring(0, 5))) {

                      idExists = true;

                      String[] facultyCourse = courses.get(i).split(",");

                      System.out.print("[" + facultyCourse[0] + "/" + facultyCourse[1] + "/" + facultyCourse[2]);

                      if (facultyCourse.length == 5) {
                        
                        System.out.println("[Online]");
                        
                      } else {

                        System.out.println(" with Labs:");
                        String[] facultyLabOne = courses.get(i + 1).split(",");
                        String[] facultyLabTwo = courses.get(i + 2).split(",");
                        String[] facultyLabThree = courses.get(i + 3).split(",");

                        System.out.println("\t[" + facultyLabOne[0] + "/" + facultyLabOne[1] + "]");
                        System.out.println("\t[" + facultyLabTwo[0] + "/" + facultyLabTwo[1] + "]");
                        System.out.println("\t[" + facultyLabThree[0] + "/" + facultyLabThree[1] + "]");

                      }
                    } 
                  }
                }
              } else if (p instanceof Student) {

                idExists = true;

                System.out.println("No faculty with this id");
                break;

              }
            }
          }
          
          if (!idExists) {

            System.out.println("Sorry no faculty found.");

          }

          break;
        } case 4: {
          
          String taID; 

          System.out.print("Enter the UCF id: ");
          taID = sc.nextLine(); 

          while (taID.length() != 7 || !taID.matches("[0-9]+")) {

            try {

              throw new IdException();

            } catch (IdException e) {

              System.out.println(e.getMessage());
              System.out.print("Enter UCF id (7 digits): ");
              taID = sc.nextLine();

            }
          }

          boolean taExistsInSystem = false; 

          for (Person person : list) {

            if(person.getId().equals(taID)) {
              
              if(person instanceof Ta) {
                System.out.println(person.getName() + " is teaching the following labs:");

                ArrayList<String> returnLabs = ((Ta)person).getLabs(); 

                for(String labCrn : returnLabs) {
                  
                  for (int i = 0; i < courses.size(); i++) {

                    if(labCrn.equals(courses.get(i).substring(0, 5))) {

                      taExistsInSystem = true; 

                      String [] labCourse = courses.get(i).split(","); 

                      System.out.println("\t[" + labCourse[0] + "/" + labCourse[1] + "]");
                      
                    }
                  }
                }

                System.out.println("Here is the classes that " + person.getName() + " is taking");
                
                taExistsInSystem = true; 
                
                ArrayList<Course> taCourses = ((Ta)person).getCourses(); 

                for(Course taCourse : taCourses) {

                  System.out.println(taCourse);

                }

              } else if(person instanceof Faculty || person instanceof Student) {

                taExistsInSystem = true; 
                System.out.println("No TA with this id");

              }
            }
          }

          if(!taExistsInSystem) {
            System.out.println("Sorry no TA found.");
          }
          
          break;
        } case 5: {

          String studentId;

          System.out.print("Enter the UCF id: ");
          studentId = sc.nextLine();

          boolean idExists = false;

          for (Person p : list) {

            if (p.getId().equals(studentId)) {

              if (p instanceof Student) {

                idExists = true;

                System.out.println("Record Found:");
                System.out.println(p.getName());
                System.out.println("Enrolled in the following lectures");

                ArrayList<Course> studentCourses = ((Student)p).getCourses();

                for (Course studentCourse : studentCourses) {

                  System.out.println(studentCourse);

                }

              } else if (p instanceof Faculty) {

                idExists = true;

                System.out.println("No student with this id");
                break;
                
              }

            }

          }

          if (!idExists) {

            System.out.println("Sorry no student found.");

          }

          break;
        } case 6: {
  
          System.out.print("Enter the CRN of the lecture to delete: ");
          String crnToDelete = sc.nextLine(); 
          String[] labsToDelete = new String[3];
  
          boolean found = false; 
          String[] deletedLecture = {"", "", ""}; 
          for(int i = 0; i < courses.size(); i++) {

            String[] parts = courses.get(i).split(","); 

            if(parts[0].equals(crnToDelete)) {
              
              found = true; 
              deletedLecture = courses.get(i).split(",");
              courses.remove(i); 

              if (courses.get(i).length() < 15) {

                labsToDelete[0] = courses.get(i).substring(0, 5);
                courses.remove(i); 
                labsToDelete[1] = courses.get(i).substring(0, 5);
                courses.remove(i); 
                labsToDelete[2] = courses.get(i).substring(0, 5);
                courses.remove(i); 

              }
              
              break; 
              
            }
          }
  
          if (!found) {
            
            System.out.println("Lecture with CRN " + crnToDelete + " not found.");

          } else {

            System.out.println("[" + deletedLecture[0] + "/" + deletedLecture[1] + "/" + deletedLecture[2] + "] Deleted"); 

          }
  
          try (BufferedWriter bw = new BufferedWriter(new FileWriter("lec.txt"))) {
            
            for (String course : courses) { 
              
              bw.write(course); 
              bw.newLine(); 
              
            }
          } catch (Exception e) {
            
            System.out.println("Error writing to the lec.txt file: " + e.getMessage());
            
          }

          for (int i = 0; i < list.size(); i++) {

            Person p = list.get(i);

            if (p instanceof Faculty) {

              ArrayList<String> facultyLectures = ((Faculty)p).getLectures();

              for (String facultyLecture : facultyLectures) {

                if (facultyLecture.equals(crnToDelete)) {

                  facultyLectures.remove(facultyLecture);
                  break;

                }
              }

            } else if (p instanceof Ta) {

              ArrayList<String> taLabs = ((Ta)p).getLabs();

              ArrayList<String> removedLabs = new ArrayList<String>();

              for (String taLab : taLabs) {

                for (String labToDelete: labsToDelete) {

                  if (taLab.equals(labToDelete)) {

                    removedLabs.add(taLab);

                  }
                }
              }

              for (String removedLab : removedLabs) {

                taLabs.remove(removedLab);

              }

              if (taLabs.size() == 0) {

                ArrayList<Course> studentCourses = ((Student)p).getCourses();

                p = new Student(p.getName(), p.getId(), "graduate");

                for (Course studentCourse : studentCourses) {

                  ((Student)p).addCourse(studentCourse);

                }

                list.set(i, p);

              }

            } if (p instanceof Student) {

              ArrayList<Course> studentLectures = ((Student)p).getCourses();

              for (Course studentLecture : studentLectures) {

                if (studentLecture.getCrn().equals(crnToDelete)) {

                  studentLectures.remove(studentLecture);
                  break;

                }
              }
            }
          }

          break;

        } case 7: {

          String copy;

          System.out.println("You have made a deletion of at least one lecture. Would you like to");
          System.out.print("print the copy of lec.txt? Enter y/Y for yes or n/N for no: ");
          copy = sc.next();

          while (!copy.equalsIgnoreCase("y") && !copy.equalsIgnoreCase("n")) {

            System.out.print("Is that yes or no? Enter y/Y for yes or n/N for no: ");
            copy = sc.next();

          }


          if(copy.equalsIgnoreCase("y")) {

            System.out.println("Printing copy of lec.txt to Printed-File.txt");
            
            File file = new File("Printed-File.txt"); 

            try {
              FileWriter writer = new FileWriter(file);
              
              PrintWriter printWriter = new PrintWriter(writer);

              for(String course : courses) {
                  printWriter.println(course); 
              }

              printWriter.close(); 
            } catch (IOException p) {

              System.out.println("An error occurred while writing to the file: " + p.getMessage());

            }

          }

          System.out.println("Bye!");

          break;
        }
      }
    }

    sc.close();

  }
}

abstract class Person {
  private String name;
  private String id;

  public Person(String name, String id) {
    this.name = name;
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}

class Faculty extends Person {
  private String rank;
  private ArrayList<String> lectures = new ArrayList<String>();

  public Faculty(String name, String id, String rank) {
    super(name, id);
    this.rank = rank;
  }

  public void addLecture(String lecture) {
    lectures.add(lecture);
  }

  public ArrayList<String> getLectures() {
    return lectures;
  }
}

class Course {
  private String crn;
  private String title;
  private String name;
  private String lab;

  public Course(String crn, String title, String name) {
    this.crn = crn;
    this.title = title;
    this.name = name;
    lab = "";
  }

  public Course(String crn, String title, String name, String lab) {
    this.crn = crn;
    this.title = title;
    this.name = name;
    this.lab = lab;
  }

  public String getCrn() {
    return crn;
  }

  public String getTitle() {
    return title;
  }

  public String getLab() {
    return lab;
  }

  public String toString() {
    if (lab.equals("")) {

      return "[" + crn + "/" + title + "/" + name + "]";

    }
    
    return "[" + crn + "/" + title + "/" + name + "]/[Lab: " + lab +"]";
  }
}

class Student extends Person  {
  private String type;
  private ArrayList<Course> courses = new ArrayList<Course>();

  public Student() {
    super("", "");
    type = "";
  }

  public Student(String name, String id, String type) {
    super(name, id);
    this.type = type;
  }

  public void addCourse(Course course) {
    courses.add(course);
  }

  public ArrayList<Course> getCourses() {
    return courses;
  }
}

class Ta extends Student {
  private String degree;
  private String advisor;
  private ArrayList<String> labs = new ArrayList<String>();

  public Ta(String name, String id, String type, String degree, String advisor) {
    super(name, id, type);
    this.degree = degree;
    this.advisor = advisor;
  }

  public void addLab(String lab) {
    labs.add(lab);
  }

  public ArrayList<String> getLabs() {
    return labs;
  }
}

class IdException extends Exception {
  public String getMessage() {
    return ">>>>>>>>>>>Sorry incorrect format. (Ids are 7 digits)";
  }
}