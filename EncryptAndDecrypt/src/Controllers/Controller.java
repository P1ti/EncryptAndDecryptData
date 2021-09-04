package Controllers;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import application.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class Controller implements Initializable {
	@FXML
	private TextField nameInput;
	@FXML
	private PasswordField passwordInput;
	@FXML
	private Button workBtn;
	@FXML
	private Button verifyData;
	@FXML
	private TableView<User> tvUsers;
	@FXML
	private TableColumn<User, Integer> tcId;
	@FXML
	private TableColumn<User, String> tcName;
	@FXML
	private TableColumn<User, String> tcPassword;
	
	// MYSQL variables for connection
	private final String url = "jdbc:mysql://localhost:3306/hashpwd";
	private final String name = "root";
	private final String pwd = "root";
	private Connection conn;
	
	//Crypting password
	private static MessageDigest md;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			showDB();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleMouseAction(MouseEvent e) throws SQLException, NoSuchAlgorithmException {
		if (e.getSource() == workBtn) {
			insertDB(); 
		}
		
		if (e.getSource() == verifyData) {
			verifyData();
		}
	}
	
	private void verifyData() throws NoSuchAlgorithmException {
		String encryptedPwd = encryptPwd(passwordInput.getText());
		System.out.println(" " + encryptedPwd + " ");
	}

	public Connection connect() {
		if (conn == null) {
			try {
				conn = DriverManager.getConnection(url, name, pwd);
				System.out.println("Successfull connection!");
			} catch(SQLException err) {
				err.printStackTrace();
			}
		}
		return conn;
	}
	
	public static String encryptPwd(String data) throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance("MD5");
		byte[] pass = data.getBytes();
		md.reset();
		byte[] digested = md.digest(pass);
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<digested.length;i++){
            sb.append(Integer.toHexString(0xff & digested[i]));
        }
		return sb.toString();
	}
	
	public ObservableList<User> getUser() throws SQLException {
		String query = "SELECT * FROM hashpwd.users";
		Connection conn;
		conn = connect();
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		User user;
		ObservableList<User> userList = FXCollections.observableArrayList();
		while (rs.next()) {
			user = new User(rs.getInt("id"), rs.getString("name"), rs.getString("pwd"));
			userList.add(user);
		}
		return userList;
	}
	
	public void insertDB() throws SQLException, NoSuchAlgorithmException {
		String encryptedPwd = encryptPwd(passwordInput.getText());
		String query = "INSERT INTO hashpwd.users (name, pwd) VALUE('" + nameInput.getText() + "','" + encryptedPwd + "');";
		executeQuery(query);
		showDB();
	}

	public void showDB() throws SQLException {
		ObservableList<User> list = getUser();
		
		tcId.setCellValueFactory(new PropertyValueFactory<User, Integer>("id"));
		tcName.setCellValueFactory(new PropertyValueFactory<User, String>("name"));
		tcPassword.setCellValueFactory(new PropertyValueFactory<User, String>("pwd"));
		
		tvUsers.setItems(list);
	}

	private void executeQuery(String query) throws SQLException {
		Connection conn = connect();
		Statement st = conn.createStatement();
		st.executeUpdate(query);
	}
}
