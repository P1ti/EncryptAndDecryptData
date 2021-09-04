package application;

public class User {
	public int id;
	public String name;
	public String pwd;
	
	public User(int id, String name, String pwd) {
		this.id = id;
		this.name = name;
		this.pwd = pwd;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPwd() {
		return pwd;
	}
}
