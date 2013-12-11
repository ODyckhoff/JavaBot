package plugin.stats.user;

import java.io.File;

import core.Channel;
import core.plugin.Plugin;
import core.utils.JSON;

public class UserListLoader extends Plugin{
	private final String DB_FILE = "Users.json";

	private String dbFile_ = new String();
	
	private UserList userList_ = new UserList();
	
	public void onCreate(Channel inChannel) throws Exception {
		dbFile_ = inChannel.getPath() + DB_FILE;
		if (new File(dbFile_).exists()){	
			if (userList_ == null) {
				userList_ = (UserList) JSON.load(dbFile_, UserList.class);
			}
		} else {
			JSON.save(dbFile_, userList_);
		}
	}
	public UserList getUserList(){
		return userList_;
	}
	
	@Override
	public String getHelpString() {
		// TODO Auto-generated method stub
		return "";
	}
}
