package application;

public class User {
	private int id;
 public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
private String nom;
 private String prenom;
 private String email;
 private String classe;
 private String ville;
 private String Sexe;

public User(int id, String nom, String prenom, String email, String classe, String ville, String sexe, String active) {
	super();
	this.id = id;
	this.nom = nom;
	this.prenom = prenom;
	this.email = email;
	this.classe = classe;
	this.ville = ville;
	Sexe = sexe;
	this.active = active;
}
private String active;
public String getNom() {
	return nom;
}
public void setNom(String nom) {
	this.nom = nom;
}
public String getPrenom() {
	return prenom;
}
public void setPrenom(String prenom) {
	this.prenom = prenom;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public String getClasse() {
	return classe;
}
public void setClasse(String classe) {
	this.classe = classe;
}
public String getVille() {
	return ville;
}
public void setVille(String ville) {
	this.ville = ville;
}
public String getSexe() {
	return Sexe;
}
public void setSexe(String sexe) {
	Sexe = sexe;
}
public String getActive() {
	return active;
}
public void setActive(String active) {
	this.active = active;
}
}
