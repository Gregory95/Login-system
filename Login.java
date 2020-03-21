package User;

import AirFlowManagment.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Font;
import javax.swing.SwingConstants;

public class Login{

	private static JFrame frame;
	private static JTextField txtUsername;
	private static JPasswordField Password_txt;
	private static String password = "";
	private static String username = "";
	private static String newSalt = "";
	private static String key = "";
	private static String last_login = "";
	private static boolean checkUser = false;
	private static boolean checkPass = false;
	private static boolean ValidCredentials = false;
	private static boolean passwordMatch = false;
	private static boolean isActive = false;

//    User user = new User();
	private Connection con;
	private Statement st;
	private ResultSet rs;
	
	static User user = new User();
	static TextFieldLimit limit = new TextFieldLimit(25);


	/**
	 * Create the application.
	 */
	public Login() {
		Connect();
		initialize();
	}

	/**
	 * Launch the application.
	 */
	public void startLogin() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException ex) {
				}
				try {
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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
	 * Initialise the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Log in");
		frame.getContentPane().setFont(new Font("Arial", Font.BOLD, 16));
		frame.setBounds(200, 200, 500, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblLoginsystem = new JLabel("Login System");
		lblLoginsystem.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginsystem.setFont(new Font("Arial", Font.BOLD, 16));
		lblLoginsystem.setBounds(162, 11, 151, 29);
		frame.getContentPane().add(lblLoginsystem);

		JLabel lblUsename = new JLabel("Username");
		JLabel lblFalseUsername = new JLabel("Invalid Username");
		lblFalseUsername.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUsename.setHorizontalAlignment(SwingConstants.CENTER);
		lblFalseUsername.setFont(new Font("Arial", Font.BOLD, 12));
		lblUsename.setFont(new Font("Arial", Font.BOLD, 16));
		lblUsename.setBounds(59, 83, 115, 33);
		lblFalseUsername.setBounds(350, 83, 115, 33);
		lblFalseUsername.setForeground(Color.red);
		frame.getContentPane().add(lblFalseUsername);
		frame.getContentPane().add(lblUsename);
		lblFalseUsername.setVisible(false);

		JLabel lblPassword = new JLabel("Password");
		JLabel lblFalsePassword = new JLabel("Invalid Password");
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblFalsePassword.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPassword.setFont(new Font("Arial", Font.BOLD, 16));
		lblFalsePassword.setFont(new Font("Arial", Font.BOLD, 12));
		lblPassword.setBounds(59, 140, 115, 33);
		lblFalsePassword.setBounds(350, 140, 115, 33);
		lblFalsePassword.setForeground(Color.red);
		frame.getContentPane().add(lblPassword);
		frame.getContentPane().add(lblFalsePassword);
		lblFalsePassword.setVisible(false);

		txtUsername = new JTextField();
		txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
		txtUsername.setBounds(217, 89, 135, 27);
		frame.getContentPane().add(txtUsername);
		txtUsername.setColumns(10);
		txtUsername.setDocument(new TextFieldLimit(25));

		Password_txt = new JPasswordField();
		Password_txt.setFont(new Font("Arial", Font.PLAIN, 14));
		Password_txt.setBounds(217, 142, 135, 25);
		Password_txt.setDocument(new TextFieldLimit(25));
		frame.getContentPane().add(Password_txt);

		// Register
		JButton Registerbtn = new JButton("Register");
		Registerbtn.setFont(new Font("Arial", Font.PLAIN, 16));
		Registerbtn.setBounds(34, 214, 98, 36);

		// Log in
		JButton btnLogin = new JButton("Login");
		btnLogin.setSelected(true);
		btnLogin.setFont(new Font("Arial", Font.PLAIN, 16));
		btnLogin.setBounds(234, 214, 98, 36);

		// Reset
		JButton Resetbtn = new JButton("Reset");
		Resetbtn.setFont(new Font("Arial", Font.PLAIN, 16));
		Resetbtn.setBounds(134, 214, 98, 36);

		// Exit
		JButton Exitbtn = new JButton("Exit");
		Exitbtn.setFont(new Font("Arial", Font.PLAIN, 16));
		Exitbtn.setBounds(334, 214, 98, 36);

		// Separator lines
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 200, 451, 4);
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 65, 451, 4);

		frame.getContentPane().add(Exitbtn);
		frame.getContentPane().add(Resetbtn);
		frame.getContentPane().add(btnLogin);
		frame.getContentPane().add(Registerbtn);
		frame.getContentPane().add(separator);
		frame.getContentPane().add(separator_1);

		frame.getRootPane().setDefaultButton(btnLogin);

		btnLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String checkUserEnabled;
//				String getSalt;
//				String userQuery;
//				String passQuery;
				String updateLastLogin;

				// User input. Fill textFields and set current username and passwords.
				username = txtUsername.getText();
				password = String.valueOf(Password_txt.getPassword());
				user.setUsername(username);
				user.setPassword(password);
				user.setLastLogin(last_login);
				last_login = user.getLastLoggin();

				// Queries
				checkUserEnabled = "SELECT * FROM [user] WHERE Username = '" + username + "'";
//				getSalt = "SELECT Salt FROM [user] WHERE Username = '" + username + "'";
//				userQuery = "SELECT Username FROM [user] WHERE Username = '" + username + "'";
				updateLastLogin = "UPDATE [user] SET Last_Login = '" + last_login + "' WHERE Username = '" + username
						+ "'";
//				passQuery = "SELECT Hashed_Code FROM [user] WHERE Password = '" + username + "'";

				try {
					PreparedStatement prepStmt = con.prepareStatement(updateLastLogin);
					rs = st.executeQuery(checkUserEnabled);
					outer: while (rs.next() && ValidCredentials == false) {
						if (rs.getString(7).equals("1")) {
							if (rs.getString(2).equals(username)) {
								isActive = true;
//								rs = st.executeQuery(getSalt);
								newSalt = rs.getString(4);
//								rs = st.executeQuery(passQuery);
								key = rs.getString(5);
								if (username.equals("admin") && password.equals("0000")) {
									ValidCredentials = true;
									checkUser = true;
									checkPass = true;
								}
								else if (checkUser == false) {
									lblFalseUsername.setVisible(false);
									if (username == null || username == "") {
										checkUser = false;
										lblFalseUsername.setVisible(true);
									} else if (rs.getString(2).equals(username)) {
										checkUser = true;
										ValidCredentials = true;

									} else {
										lblFalseUsername.setVisible(true);
										txtUsername.selectAll();
										checkUser = false;
										ValidCredentials = false;
									}
								}
//							key = PasswordUtils.hash(password, newSalt);
//							rs = st.executeQuery(passQuery);
								if (checkPass == false) {
									passwordMatch = PasswordUtils.verifyPassword(password, key, newSalt);
									lblFalsePassword.setVisible(false);
									if (checkUser == true && passwordMatch == true)
										checkPass = true;
									else {
										lblFalsePassword.setVisible(true);
										Password_txt.selectAll();
										checkPass = false;
										ValidCredentials = false;
									}
								}
//								if (username.equals("admin") && password.equals("0000")) {
//									frame.setVisible(false);
//									ValidCredentials = true;
//									prepStmt.executeUpdate();
//									@SuppressWarnings("unused")
//									UserInterface UI = new UserInterface();
//								}
								if (checkUser == true && checkPass == true) {
									frame.setVisible(false);
									ValidCredentials = true;
									prepStmt.executeUpdate();
									@SuppressWarnings("unused")
									UserInterface UI = new UserInterface();
								}

							} else {
								JOptionPane.showMessageDialog(null, "Invalid Credentials", "Login Error",
										JOptionPane.OK_OPTION);
								checkUser = false;
								ValidCredentials = false;
								lblFalseUsername.setVisible(true);
								user.setUsername("");
								user.setPassword("");
								break outer;
							}

						} else {
							JOptionPane.showMessageDialog(null,
									"User is not active.  Please activate user so you can log in.", "User Disabled",
									JOptionPane.OK_OPTION);
							break outer;
						}

					}

					if (ValidCredentials == false && isActive == false) {
						JOptionPane.showMessageDialog(null, "Invalid Credentials", "Login Error",
								JOptionPane.OK_OPTION);
						if (checkUser == true)
							lblFalsePassword.setVisible(true);
						else
							lblFalseUsername.setVisible(true);
						checkUser = false;
						ValidCredentials = false;
					}

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Unknown Error", "Unknown error", JOptionPane.OK_OPTION);
					System.out.println(ex.getMessage());
					ex.printStackTrace();
//					System.exit(0);
				}
			}
		});
		Resetbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {

				txtUsername.setText(null);
				Password_txt.setText(null);
			}
		});
		Exitbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				JFrame frmLoginSystem = new JFrame("Exit");
				if (JOptionPane.showConfirmDialog(frmLoginSystem, "Would like to exit the system?", "Login System",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_NO_OPTION) {
					System.exit(0);
				}
			}
		});

		Registerbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				frame.setVisible(false);
				RegistrationFrame reg = new RegistrationFrame();
				reg.startRegistration();

			}
		});
	}

}
