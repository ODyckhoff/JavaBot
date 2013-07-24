import java.io.File;
import java.io.IOException;

import core.Channel;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.Plugin;
import core.utils.Details;
import core.utils.IRC;
import core.utils.IRCException;
import core.utils.JSON;
import addons.users.User;
import addons.users.UserList;

public class Users implements Plugin
{

	private final String dbFile_loc = "Users.json";
	private String dbFile = new String();
	
	@Override
	public String name() 
	{
		return "Users";
	}
	
	@Override
	public void onCreate(Channel in_channel) throws Exception
	{
		dbFile = in_channel.getPath() + dbFile_loc;
		if (new File(dbFile).exists())
			UserList.setInstance((UserList)JSON.loadGSON(dbFile,UserList.class));
		else
			JSON.saveGSON(dbFile, UserList.getInstance());
	}

	@Override
	public void onMessage(Message in_message) throws IRCException, IOException
	{
		JSON.saveGSON(dbFile, UserList.getInstance());
		UserList.getInstance().msgSent(in_message);
	}

	@Override
	public void onJoin(Join in_join) throws Exception
	{
		JSON.saveGSON(dbFile, UserList.getInstance());
		IRC irc = IRC.getInstance();
    	User userOBJ = UserList.getInstance().getUser(in_join.getUser());
    	if (userOBJ != null)
    	{
    		userOBJ.incjoins(in_join.getHost());
    		String reply = "%s Has joined %s times";
    		irc.sendPrivmsg(in_join.getChannel(), String.format(reply, 
    				in_join.getUser(), userOBJ.getJoins()));
    	}
    	else
    	{
    		String botname = Details.getInstance().getNickName();
    		if (!botname.equalsIgnoreCase(in_join.getUser()))
    		{
    			String reply = "Hello %s and welcome! Don't break things!";
    			irc.sendPrivmsg(in_join.getChannel(), 
    					String.format(reply, in_join.getUser()));
    		}
    	}
	}

	@Override
	public void onQuit(Quit in_quit) throws Exception
	{
		IRC irc = IRC.getInstance();
		JSON.saveGSON(dbFile, UserList.getInstance());

    	User userOBJ = UserList.getInstance().getUser(in_quit.getUser());
    	if (userOBJ != null)
    	{
    		userOBJ.incQuits();
    		String reply = "%s Has quit %s times";
    		irc.sendPrivmsg(in_quit.getChannel(), String.format(reply, 
    				in_quit.getUser(), userOBJ.getQuits()));
    	}
	}

    @Override
    public void onKick(Kick in_kick) throws Exception 
	{
    	IRC irc = IRC.getInstance();

		User userOBJ = UserList.getInstance().getUser(in_kick.getKicked());
    	if (userOBJ != null)
    	{
    		userOBJ.incKicks();
    		String reply = "%s Has be kicked %s times";
    		irc.sendPrivmsg(in_kick.getChannel(), String.format(reply,
    				in_kick.getKicked(), userOBJ.getKicks()));
    	}
	}
    
	@Override
	public String getHelpString()
	{
		// TODO Auto-generated method stub
		return "Help string for users plugin";
	}

	@Override
	public void onTime() throws Exception {}
	@Override
	public void rawInput(String in_str) throws Exception {}
}
