package User;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
//import javax.swing.SwingUtilities;

import AirFlowManagment.User;

public class RegistrationFrame {

	private static JFrame registration_frame;
	private static JTextField Username_txt;
	private static JPasswordField Password_txt;
	private static JPasswordField Confirm_Password_txt;

	//Database Attributes
	private static String password;
	private static String username = "";
	private static String last_login = "";
	private static String Salt = "";
    private static String hashedPass = "";
	private static int counter = 1;
	private static boolean registered = false;
	private static boolean enabled = false;
	
	
	private static CharSequence confirm_password;
	private static boolean invalidUser = true;
//	private static boolean invalidPass = false;
	private static boolean clicked = false;

	// Encryption class
	static PasswordUtils enc = new PasswordUtils();
//	private static String splitedPassword;
	private static String new_Password = "";
	private static String hashedPassword = "";
	private static String securePassword = "";

	// Database
	private Connection con;
	private Statement st;
	private ResultSet rs;

	User newUser = new User();
	Login login = new Login();
	
	/**
	 * Create the application.
	 */
	public RegistrationFrame() {
		showRegistrationFrame();
		Username_txt.setEditable(true);
		Password_txt.setEnabled(true);
	}

	/**
	 * Database connection
	 */
	void Connect() {
		try {
			// Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager
					.getConnection("jdbc:sqlserver://localhost;databasename=AIRPORT_SYSTEM_V1;integratedSecurity=true");
			st = con.createStatement();
			System.out.println("Connected");
		} catch (Exception ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void showRegistrationFrame() {
		registration_frame = new JFrame("Registration");
		Image registrationIcon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\grego\\Desktop\\Computer Science\\Coding\\JavaProjects\\AirportFlowControllSystem\\icons\\RegisterIcon.jpg");
		registration_frame.setIconImage(registrationIcon);
		registration_frame.getContentPane().setFont(new Font("Arial", Font.BOLD, 16));
		registration_frame.setBounds(200, 200, 500, 300);
		registration_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		registration_frame.getContentPane().setLayout(null);

		JLabel lblLoginsystem = new JLabel("User Registration");
		lblLoginsystem.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginsystem.setFont(new Font("Arial", Font.BOLD, 16));
		lblLoginsystem.setBounds(162, 11, 151, 29);
		registration_frame.getContentPane().add(lblLoginsystem);

		JLabel lblUsename = new JLabel("Username");
		lblUsename.setHorizontalAlignment(SwingConstants.LEFT);
		lblUsename.setFont(new Font("Arial", Font.BOLD, 14));
		lblUsename.setBounds(50, 63, 130, 19);
		registration_frame.getContentPane().add(lblUsename);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setHorizontalAlignment(SwingConstants.LEFT);
		lblPassword.setFont(new Font("Arial", Font.BOLD, 14));
		lblPassword.setBounds(50, 103, 130, 19);
		registration_frame.getContentPane().add(lblPassword);

		JLabel lblConfirmPassword = new JLabel("Confirm Password");
		lblConfirmPassword.setHorizontalAlignment(SwingConstants.LEFT);
		lblConfirmPassword.setFont(new Font("Arial", Font.BOLD, 14));
		lblConfirmPassword.setBounds(50, 143, 130, 19);
		registration_frame.getContentPane().add(lblConfirmPassword);

		Username_txt = new JTextField();
		Username_txt.setFont(new Font("Arial", Font.PLAIN, 12));
		Username_txt.setBounds(247, 63, 165, 20);
		Username_txt.setColumns(10);
		Username_txt.setDocument(new TextFieldLimit(25));
		registration_frame.getContentPane().add(Username_txt);

		Password_txt = new JPasswordField();
		Password_txt.setFont(new Font("Arial", Font.PLAIN, 12));
		Password_txt.setBounds(247, 103, 165, 20);
		Password_txt.setDocument(new TextFieldLimit(25));
		registration_frame.getContentPane().add(Password_txt);

		Confirm_Password_txt = new JPasswordField();
		Confirm_Password_txt.setFont(new Font("Arial", Font.PLAIN, 12));
		Confirm_Password_txt.setBounds(247, 143, 165, 20);
		Confirm_Password_txt.setDocument(new TextFieldLimit(25));
		registration_frame.getContentPane().add(Confirm_Password_txt);

		// Separator lines
		// footer
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 200, 451, 4);
		// header
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 45, 451, 4);

		registration_frame.getContentPane().add(separator);
		registration_frame.getContentPane().add(separator_1);

		// Log in
		JButton btnCreate = new JButton("Create");
		btnCreate.setSelected(true);
		btnCreate.setFont(new Font("Arial", Font.PLAIN, 16));
		btnCreate.setBounds(134, 214, 98, 36);

		// Reset
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setFont(new Font("Arial", Font.PLAIN, 16));
		btnCancel.setBounds(234, 214, 98, 36);

		registration_frame.getContentPane().add(btnCreate);
		registration_frame.getContentPane().add(btnCancel);
		registration_frame.getRootPane().setDefaultButton(btnCreate);

		// write queries to insert the new (VALID) data in the database
		btnCreate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				String sqlQuery = "";
				Connect();

				try {
					if (clicked == false) {
						if (getRegistrationInput() == true) {
							sqlQuery = "INSERT INTO [User] (ID, Username, Password, Salt, Hashed_Code ,Last_Login, Enabled)"
									+ "VALUES (?,?,?,?,?,?,?)";

							PreparedStatement prepStmt = con.prepareStatement(sqlQuery);
							String countQuery = "SELECT COUNT(*) as 'rowCount' FROM [user]";
							rs = st.executeQuery(countQuery);
							rs.next();
							counter = rs.getInt("rowCount");
							rs.close();

							username = newUser.getUsername();
							password = (String) newUser.getPassword();
							last_login = newUser.getLastLoggin();
							Salt = newUser.getExistingSalt();
							hashedPass = newUser.getSecurePass();
							newUser.setEnabled(enabled);

							prepStmt.setInt(1, counter);
							prepStmt.setString(2, username);
							prepStmt.setString(3, password);
							prepStmt.setString(4, Salt);
							prepStmt.setString(5, hashedPass);
							prepStmt.setString(6, last_login);
							prepStmt.setBoolean(7, enabled);
							prepStmt.executeUpdate();
							JOptionPane.showMessageDialog(null, "User Created!");
							login.startLogin();
						} else {
							JOptionPane.showMessageDialog(null, "Could not create user!", "Creation failed",
									JOptionPane.CANCEL_OPTION);
							Username_txt.setText("");
							Password_txt.setText("");
							Confirm_Password_txt.setText("");
//							getRegistrationInput();
						}
					} else {
						Username_txt.setText("");
						Password_txt.setText("");
						Confirm_Password_txt.setText("");
						clicked = true;
						getRegistrationInput();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("Unknown Error" + e.getMessage());
					e.printStackTrace();
					Username_txt.selectAll();
					Password_txt.selectAll();
					Confirm_Password_txt = null;
				}
			}

		});
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFrame frmLoginSystem = new JFrame("Exit");
				if (JOptionPane.showConfirmDialog(frmLoginSystem, "Would like to cancel the process?", "Registration",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_NO_OPTION) {
					login.startLogin();
					registration_frame.setVisible(false);
					
				}
			}
		});
	}

	public boolean getRegistrationInput() throws SQLException {

//		SwingUtilities.updateComponentTreeUI(registration_frame);

		Salt = PasswordUtils.getSalt(128);
		newUser.setSalt(Salt);

//		while (registered == false) {
		username = Username_txt.getText();
		newUser.setUsername(username);
		if (Username_txt.getText().isEmpty() == true || newUser.getUsername() == null) {
			JOptionPane.showMessageDialog(null, "Username field must be correct", "Invalid Username",
					JOptionPane.ERROR_MESSAGE);
			Username_txt.selectAll();
			invalidUser = true;
		}

//		password = Password_txt.getText();
		password = String.valueOf(Password_txt.getPassword());
		// validations

		
		if (newUser.password_validate(password) == true) {
			new_Password = PasswordUtils.hash(password, Salt);
			newUser.setSecuredPass(new_Password);
			securePassword = PasswordUtils.generateSecurePassword(new_Password, Salt);
			hashedPassword = securePassword;
//		while (getPassword() == "") {
//		password = Password_txt.getText();
			newUser.setPassword(hashedPassword);
			invalidUser = false;
		} else {
			JOptionPane.showMessageDialog(null, "Password field must be correct", "Invalid Password",
					JOptionPane.ERROR_MESSAGE);
			Password_txt.selectAll();
			invalidUser = true;
		}
		confirm_password = String.valueOf(Confirm_Password_txt.getPassword());
		if (!confirm_password.toString().contentEquals(password.toString()) || Confirm_Password_txt == null) {
			JOptionPane.showMessageDialog(null, "Confirm Password field must be correct", "Password did not Match",
					JOptionPane.ERROR_MESSAGE);
		}
		if (confirm_password.toString().equals(password.toString()) && invalidUser == false) {
			registration_frame.setVisible(false);
			enabled = true;
			registered = true;
//			invalidUser = false;
			newUser.setLastLogin(last_login);
		} else {
			JOptionPane.showMessageDialog(null, "Invalid input", "Registration Failed", JOptionPane.WARNING_MESSAGE);
			Confirm_Password_txt.selectAll();
			registered = false;
		}
//		}
		return registered;
	}

	/**
	 * Launch the application.
	 */
	public void startRegistration() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					registration_frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

//	@SuppressWarnings("null")
//	public void Connect() throws SQLException {
//		this.jdbcDriver = "";
//		this.jdbcURL = "";
//		try {
//			// The SQL Server JDBC Driver is in
//			// C:\Program Files\Microsoft JDBC Driver 6.0 for SQL
//			// Server\sqljdbc_6.0\enu\auth\x64
//			this.jdbcDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//
//			// The JDBC connection URL which allows for Windows authentication is defined
//			// below.
//			this.jdbcURL = "jdbc:sqlserver://localhost;databasename=AIRPORT_SYSTEM_V1;integratedSecurity=true;";
//			// To make Windows authentication work we have to set the path to
//			// sqljdbc_auth.dll at the command line
//			// Connect to the database
//			this.con = DriverManager.getConnection(jdbcURL);
//
//			// declare the statement object
//			this.st = con.createStatement();
//
//			System.out.println("Connected to the database");
//
//		} catch (SQLException err) {
//			System.err.println("Error connecting to the database");
//			err.printStackTrace(System.err);
//			System.out.println("SQLException: " + err.getMessage());
//			con.close();
//			System.exit(0);
//		}
//
//	}
}
