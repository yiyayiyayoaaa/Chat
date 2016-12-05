package com.microcardio.chat.po;

/**
 *   常量
 */
public interface Constants {
	String SERVER_ADDRESS = "101.200.53.234";
	String UPLOAD_ADDRESS = "http://"+ SERVER_ADDRESS + ":8080/upload";
	String UPLOAD_URL = UPLOAD_ADDRESS + "/Upload";
	String FILE_PATH = UPLOAD_ADDRESS;
	int SERVER_PORT = 8888;     //服务器端口号
	int CMD_REGISTER = 0;       //注册
	int CMD_LOGIN = 1;          //登录
	int GET_PORTRAIT = 111;
	int CMD_LOGOUT = 2;         //退出
	int CMD_CHAT = 3;           //聊天
	int CMD_CHAT_IMG = 333;   //聊天信息为图片
	int LOGIN_SUCCESS = 4;     //登录成功
	int LOGIN_ERROR = 5;       //登录失败
	int REGISTER_SUCCESS = 6; //注册成功
	int REGISTER_ERROR = 7;   //注册失败
	int CHAT_OFFLINE = 8;     //用户离线
	int LOGIN_REMIND = 9;     //用户登录提醒
	int USER_LIST = 10;       //用户列表
	int USER_CHAT = 11;       //用户聊天记录
	int VALIDATE_USERNAME = 12; //验证用户名
	int IS_EXIST = 13;     //已存在
	int IS_NOT_EXIST = 14; //不存在
	int EDIT_ME = 15;    //修改信息
	int EDIT_SUCCESS = 16; //修改成功
	int EDIT_ERROR = 17;   //修改失败
	int EditMe_RETURN = 18;
	int USER_UPDATE = 19;  //用户信息改变

	int FILE = 20;
	int IMAGE = 21;
	int Message_List = 22;
	int TAKE_PHOTO = 23;
}
