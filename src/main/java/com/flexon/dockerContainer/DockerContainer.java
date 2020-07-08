package com.flexon.dockerContainer;

import java.sql.Connection;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Scanner;
import static java.sql.DriverManager.getConnection;
import java.sql.ResultSet;

public class DockerContainer {
    private final static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private final static String DB_URL = "jdbc:mysql://54.241.143.16:3306/bank";
    private final static String USER = "root";
    private final static String PASS = "passwd";
    static Connection conn = null;

    public static void main( String[] args ) throws SQLException, ClassNotFoundException, IOException {
        // TODO Auto-generated method stub
        DockerContainer dockerContainer = new DockerContainer();
        Class.forName(JDBC_DRIVER);
        System.out.println("Connecting to database...");
        dockerContainer.checkConnection();
        System.out.println("Connecting to database successfully!");
        //show tables
        dockerContainer.showTable();
        //select AddUser, DeleteUer, Print, Exit
        System.out.println("\nSelect \"add\" or \"delete\" or \"exit\" or \"print\" : ");
        dockerContainer.choose();
        conn.close();


    }

    protected void checkConnection() throws SQLException {
        conn = getConnection(DB_URL, USER, PASS);
    }

    protected void showTable() throws SQLException {
        //System.out.println("AccountId FirtName LastName Age");
        Statement stmt = conn.createStatement();
        String sql = "select * from TestJava;";
        ResultSet rs = stmt.executeQuery(sql);
        //int columnsNumber = rs.getMetaData().getColumnCount();

        while (rs.next()) {
            //Retrieve by column name
            int id  = rs.getInt("AccountId");
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            int age = rs.getInt("Age");

            //Display values
            System.out.print("AccountID: " + id);
            System.out.print(", firstName: " + firstName);
            System.out.print(", LastName: " + lastName);
            System.out.println(", Age: " + age);
        }

    }

    protected void choose() throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);
        DockerContainer dockerContainer = new DockerContainer();
        while (true){
            String answer = scanner.nextLine();
            if (answer.equals("add")){
                dockerContainer.addUser();
                System.out.println("Select \"add\" or \"delete\" or \"exit\" or \"print\" : ");
            }
            else if (answer.equals("delete")){
                dockerContainer.deleteUser();
                System.out.println("Select \"add\" or \"delete\" or \"exit\" or \"print\" : ");
            }
            else if (answer.equals("print")){
                dockerContainer.printUser();
                System.out.println("Select \"add\" or \"delete\" or \"exit\" or \"print\" : ");
            }
            else if (answer.equals("exit")){
                dockerContainer.showTable();
                System.out.println("exited");

                break;
            }
            else {
                System.out.println("Select \"add\" or \"delete\" or \"exit\" or \"print\" : ");
            }

        }

    }

    protected void addUser() throws SQLException {
        DockerContainer dockerContainer = new DockerContainer();
        dockerContainer.checkConnection();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your first name: ");
        String firstName = scanner.nextLine();
        System.out.println("Enter your last name: ");
        String lastName = scanner.nextLine();
        System.out.println("Enter your age: ");
        int age = scanner.nextInt();

        String sql = "select * from TestJava;";
        Statement stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery(sql);
        rs.last();
        int rowNumber = rs.getRow() + 1;
        System.out.println(rowNumber);
        // the mysql insert statement
        String query = " insert into TestJava (AccountId, firstName, lastName, age)"
                + " values ( ?,?, ?, ?)";
        // create the mysql insert preparedstatement
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt (1, rowNumber);
        preparedStmt.setString (2, firstName);
        preparedStmt.setString   (3, lastName);
        preparedStmt.setInt    (4, age);
        // execute the preparedstatement
        preparedStmt.execute();
        dockerContainer.showTable();
    }

    protected void deleteUser() throws SQLException{
        //check connection
        DockerContainer dockerContainer = new DockerContainer();
        dockerContainer.checkConnection();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the AccountId you want to delete ");
        int accountId = scanner.nextInt();
        String query = "delete from TestJava WHERE AccountId = ?;";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt (1, accountId);
        preparedStmt.execute();
        dockerContainer.showTable();
    }
    protected void printUser() throws SQLException, IOException {
        DockerContainer dockerContainer = new DockerContainer();
        dockerContainer.checkConnection();
        File file = new File("user_table.csv");
        if (file.createNewFile()){
            System.out.println("File created: " + file.getName());
        }else {
            System.out.println("File already exist!");
        }
        PrintWriter pw = new PrintWriter(file);
        StringBuilder sb = new StringBuilder();
        String query="select * from TestJava";
        PreparedStatement ps= conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        sb = sb.append("AccountId"+","+"firstName\r\n");
        while(rs.next()){
            sb.append(rs.getInt("AccountId"));
            sb.append(",");
            sb.append(rs.getString("firstName"));
            sb.append("\r\n");
        }

        pw.write(sb.toString());
        pw.close();
        System.out.println("finished");

    }
}

