package com.microcardio.chat.po;

/**
 * 该类用来封装用户的基本信息
 * @author AMOBBS
 *
 */
public class User {
	private String username; //用户名
	private String password; //密码
	private String nickname;  //昵称
	private int portrait;    //头像
	private String newInfo;  //最新消息
	private String newDate;  //最新消息

	public User() {
		// TODO Auto-generated constructor stub
	}
	
	
	public User(String username, String password, String nickname, int portrait) {
		super();
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.portrait = portrait;
	}


	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getPortrait() {
		return portrait;
	}
	public void setPortrait(int portrait) {
		this.portrait = portrait;
	}

	public String getNewInfo() {
		return newInfo;
	}

	public void setNewInfo(String newInfo) {
		this.newInfo = newInfo;
	}

	public String getNewDate() {
		return newDate;
	}

	public void setNewDate(String newDate) {
		this.newDate = newDate;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", nickname=" + nickname + ", portrait="
				+ portrait + "]";
	}
	
}
