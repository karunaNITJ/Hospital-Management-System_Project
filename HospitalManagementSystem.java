package HospitalManagmentSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql:///hospital";
    private static final String username = "root";
    private static final String password = "Sudu@8757";

    public static void main(String[]args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        try{
          Connection connection = DriverManager.getConnection(url,username,password);
          Patient patient = new Patient(connection,scanner);
          Doctor doctor = new Doctor(connection);
          while(true) {
              System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
              System.out.println("1. Add Patient");
              System.out.println("2. view patients");
              System.out.println("3. view Doctors");
              System.out.println("4. Book Appointment");
              System.out.println("5. Exit");
              System.out.println("Enter your choice: ");
              int choice = scanner.nextInt();

              switch(choice) {
                  case 1:
                          //Add Patient
                          patient.addPatient();
                          System.out.println();
                          break;
                  case 2:
                          // view patient
                          patient.viewPatients();
                          System.out.println();
                          break;
                  case 3:
                          // view Doctors
                          doctor.viewDoctors();
                          System.out.println();
                          break;
                  case 4:
                          // Book Appointment
                          bookAppointment(patient,doctor,connection,scanner);
                          System.out.println();
                          break;

                  case 5:
                      System.out.println("THANK YOU FOR USING HOSPITAL MANAGEMENT SYSTEM:");
                          return;
                  default:
                      System.out.println("Enter Valid choice!");
                      break;
              }
          }
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner) {
        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter Appointment Date (YYYY-MM-DD):  ");
        String appointmentDate = scanner.next();

        if(patient.getPatientById(patientId) && doctor.getPatientById(doctorId)) {
           if(checkDoctorAvailability(doctorId,appointmentDate,connection)) {
              String appointmentQuery = "Insert into appointments(patient_id,doctor_id,appointment_date) values(?,?,?)";
              try {
                  PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);                  preparedStatement.setInt(1,patientId);
                  preparedStatement.setInt(2,doctorId);
                  preparedStatement.setString(3,appointmentDate);
                int rowAffected = preparedStatement.executeUpdate();
                if(rowAffected>0) {
                    System.out.println("Appointment Booked !");
                }else{
                    System.out.println("Failed to Book Appointment!");
                }

              }
              catch(SQLException e) {
                  e.printStackTrace();
              }

           } else {
               System.out.println("Doctor not available on this date: ");
           }

        } else {
            System.out.println("Either doctor or patient doesn't exit: ");
        }

    }

    public static boolean checkDoctorAvailability(int doctorId,String appointmentDate,Connection connection) {
        String query = "select count(*) from appointments where doctor_id=? and appointment_date=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                int count = resultSet.getInt(1);
                if(count==0) {
                    return true;
                } else{
                    return false;
                }
            }

        }catch(SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
