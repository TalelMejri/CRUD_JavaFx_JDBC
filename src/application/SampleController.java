package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class SampleController implements Initializable {
	@FXML
	TextField search;
	@FXML
	ComboBox<String> ListClasses;
	@FXML
	ComboBox<String> ListVilles;
	@FXML
	TextField nom;
	@FXML
	TextField prenom;
	@FXML
	TextField email;
	@FXML
	RadioButton man;
	@FXML
	RadioButton women;
	@FXML
	ComboBox<String> listRoles;
	@FXML
	Label error;
	@FXML
	TableView<User> tabUser;
	
	ObservableList<User> users=FXCollections.observableArrayList();
	
	String url="jdbc:mysql://localhost/crud_jdbc";
	String root="root";
	String pwd="";
	Statement stmt=null;
	Connection conn=null;
	PreparedStatement prep=null;
	
	public SampleController() {
		this.Connected();
	}
	
	public void Connected() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.conn=DriverManager.getConnection(url, root, pwd);
		}catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	ObservableList<String> listclasse=FXCollections.observableArrayList("dsi22","dsi22");
	ObservableList<String> listVille=FXCollections.observableArrayList("Bahra","Bizerte");
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ListClasses.setItems(listclasse);
		ListVilles.setItems(listVille);
		this.GetAllRoles();
		this.Affiche();
	}
	
	public void AddUser() {
		if(nom.getText().isEmpty()) {
			error.setText("Nom Required");
		}else if (prenom.getText().isEmpty()) {
			error.setText("prenom Required");	
		}else if (email.getText().isEmpty()) {
			error.setText("email Required");
		}else if(!email.getText().contains("@") && !email.getText().contains(".") ) {
			error.setText("email Invalid");
		}else if(ListClasses.getSelectionModel().getSelectedItem()==null) {
			error.setText("Classe Required");
		}else if(ListVilles.getSelectionModel().getSelectedItem()==null) {
			error.setText("Ville Required");
		}else if(man.isSelected()==false && women.isSelected()==false) {
			error.setText("Sex Required");
		}else if(listRoles.getSelectionModel().getSelectedItem()==null) {
			error.setText("Role Required");
		}else {
			String req="INSERT INTO `user`(`nom`, `prenom`, `email`, `sex`, `Classe`, `Ville`, `role`, `Active`) VALUES (?,?,?,?,?,?,?,?)";
			String RoleName=listRoles.getSelectionModel().getSelectedItem();
			String req2="select id from role where RoleName=?";
			try {
				PreparedStatement prep=this.conn.prepareStatement(req2);
				prep.setString(1, RoleName);
				ResultSet res2=prep.executeQuery();
				int id_role=0;
				while(res2.next()) {
					id_role=res2.getInt(1);
				};
			    PreparedStatement prep2=this.conn.prepareStatement(req);
			    prep2.setString(1, nom.getText());
			    prep2.setString(2, prenom.getText());
			    prep2.setString(3, email.getText());
			    prep2.setString(4, man.isSelected() ? "Man" : "Women");
			    prep2.setString(5, ListClasses.getSelectionModel().getSelectedItem());
			    prep2.setString(6, ListVilles.getSelectionModel().getSelectedItem());
			    prep2.setInt(7, id_role);
			    prep2.setBoolean(8, false);
			    prep2.executeUpdate();
			    Alert alert=new Alert(AlertType.INFORMATION);
			    alert.setHeaderText("Sign up");
			    alert.setContentText("Votre compte bien enregiste "+nom.getText());
			    alert.show();
			    nom.clear();
			    prenom.clear();
			    email.clear();
			    listRoles.getSelectionModel().clearSelection();
			    ListVilles.getSelectionModel().clearSelection();
			    ListClasses.getSelectionModel().clearSelection();
			    man.setSelected(false);
			    women.setSelected(false);
			    this.Affiche();
			}catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void GetAllRoles() {
		ObservableList<String> liste=FXCollections.observableArrayList();
		String requete="SELECT * FROM role";
		try {
			stmt=this.conn.createStatement();
			ResultSet resRoles=stmt.executeQuery(requete);
			while(resRoles.next()) {
				liste.add(resRoles.getString(2));
			}
			listRoles.setItems(liste);
		}catch(SQLException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void Affiche() {
		tabUser.getItems().clear();
		String req="SELECT * FROM USER";
		String reqName="SELECT * from role where id=?";
		try {
			stmt=this.conn.createStatement();
			ResultSet resUsers=stmt.executeQuery(req);
			while(resUsers.next()){
				String role="";
				PreparedStatement prepName=this.conn.prepareStatement(reqName);
				prepName.setInt(1, resUsers.getInt(8));
				ResultSet resOfRole=prepName.executeQuery();
				String role_name="";
				while(resOfRole.next()) {
					role_name=resOfRole.getString(2);
				}
				User user=new User(resUsers.getInt(1), resUsers.getString(2), resUsers.getString(3), resUsers.getString(4), resUsers.getString(6), resUsers.getString(7), resUsers.getString(5),resUsers.getBoolean(9) ? "Active" : "Not Yet");
				users.add(user);
			}
			tabUser.setItems(users);
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void deleteUser() {
		User user=tabUser.getSelectionModel().getSelectedItem();
		int id=user.getId();
		String reqDelete="delete from user where id=?";
		try {
			Alert alertdelete=new Alert(AlertType.CONFIRMATION);
			alertdelete.setContentText("Delete with success");
			Optional<ButtonType> result =  alertdelete.showAndWait();
			if(result.get()==ButtonType.OK) {
				PreparedStatement prepdelete=this.conn.prepareStatement(reqDelete);
				prepdelete.setInt(1, id);
				prepdelete.executeUpdate();
				tabUser.getItems().remove(user);
			}
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void updateActive() {
		User user=tabUser.getSelectionModel().getSelectedItem();
		int id=user.getId();
		String reqUpdate="update user set Active=? where id=?";
		try {
			PreparedStatement prep=this.conn.prepareStatement(reqUpdate);
			prep.setBoolean(1, true);
			prep.setInt(2, id);
			prep.executeUpdate();
			this.Affiche();
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void searchByName(){
		String reqSearch="select * from user where nom like ?";
		String reqName1="SELECT * from role where id=?";
		try {
			PreparedStatement prep=this.conn.prepareStatement(reqSearch);
			prep.setString(1, search.getText());
			ResultSet resultatSearch=prep.executeQuery();
			/*while(resultatSearch.next()) {
				System.out.println(resultatSearch.getString("nom"));
			}*/
			/*ResultSetMetaData resmdr=(ResultSetMetaData) resultatSearch.getMetaData();
			int column_count=resmdr.getColumnCount();*/
			//System.out.println(resultatSearch.getFetchSize());
			//if(resultatSearch.getFetchSize()>0) {
				this.tabUser.getItems().clear();
				while(resultatSearch.next()) {
					String role="";
					PreparedStatement prepName=this.conn.prepareStatement(reqName1);
					prepName.setInt(1, resultatSearch.getInt(8));
					ResultSet resOfRole=prepName.executeQuery();
					String role_name="";
					while(resOfRole.next()) {
						role_name=resOfRole.getString(2);
					}
					User user=new User(resultatSearch.getInt(1), resultatSearch.getString(2), resultatSearch.getString(3), resultatSearch.getString(4), resultatSearch.getString(6), resultatSearch.getString(7), resultatSearch.getString(5),resultatSearch.getBoolean(9) ? "Active" : "Not Yet");
					users.add(user);
				}
				tabUser.setItems(users);
				if(search.getText().length()==0) {
					this.Affiche();
				}
			//}
		}catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

}
