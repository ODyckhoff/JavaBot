import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import event.Join;
import event.Kick;
import event.Message;
import event.Quit;
import addons.Reminders;
import addons.RemindersList;
import addons.User;
import addons.UserList;
import plugin.PluginTemp;
import program.Details;
import program.IRC;
import program.IRCException;
import program.JSON;


public class Reminder implements PluginTemp
{
	private RemindersList rl = new RemindersList();
	private static final String cfgFile = "Reminders.json";
	
	@Override
	public String name() 
	{
		return "Reminder";
	}
	
	@Override
	public void onCreate() throws IRCException, IOException 
	{
		if (new File(cfgFile).exists())
			rl = (RemindersList)JSON.loadGSON(cfgFile, RemindersList.class);
		else
			JSON.saveGSON(cfgFile, rl);
	}

	@Override
	public void onTime() throws Exception
	{	
		IRC irc = IRC.getInstance();
		
		String[] channels = Details.getIntance().getChannels();
		
		Reminders[] reminders = rl.getReminders(new Date());
		
		for (int i = 0; i < reminders.length; i++)
			for (int j = 0; j < channels.length;j++)
				irc.sendPrivmsg(channels[j], reminders[i].getReminder());
	}

	@Override
	public void onMessage(Message in_message) throws Exception
	{
		IRC irc = IRC.getInstance();
		UserList ul = UserList.getInstance();
		
		String user = in_message.getUser(), 
				channel = in_message.getChannel(), 
				message = in_message.getMessage();
		    
		if (message.matches("(\\.reminder)\\s([a-zA-Z0-9]*)\\s([a-zA-Z\\w\\d\\s]*)"))
		{
		    Matcher r = Pattern.compile("\\.reminder\\s([a-zA-Z0-9]*)\\s([a-zA-Z\\w\\d\\s]*)",
		    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
		    if (r.find())
		    {
		    	ul.addReminder(r.group(1),  
		    			r.group(1) + ": " + user + " Said to you earlier " + r.group(2));
		    	
		    	irc.sendPrivmsg(channel, 
		    			user + ": I will remind " + r.group(1) + " next time they are here.");
		    }
		}
		else if(message.matches("^\\.reminder ([\\d//:]*) ([\\d:]*).*"))
		{
		    Matcher m = Pattern.compile("^\\.reminder ([\\d//:]*) ([\\d:]*)(.*)",
		    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
		    
		    if (m.find())
		    {
		    	
		    	String date = m.group(1), reminder = m.group(3);
		    	Date eventtime;
		    	
		    	if (date.matches("([0-3][0-9]/[0-1][0-9]/20[1-9][0-9])"))
		    		date += " " + m.group(2);	
		    	else
		    		date = new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + " " + m.group(1);
		    	
		    	eventtime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).parse(date);
	    		rl.addReminder(reminder, eventtime);
	    		irc.sendPrivmsg(channel, user + ": Reminder Added.");
		    }
		}
		else if(message.matches("^\\.help") || message.matches("^\\."))
	    {
			irc.sendPrivmsg(channel, "REMINDER: " +
							".reminder *username* *Message* - leave a message for another member : " +
							".reminder 00:00 *Message* - Leave reminder for the channel to view later today : " +
							".reminder 01/01/1970 00:00 *Message* Leave a reminder for the future on a different date : "
							);
	    }
		else
		{
			User userOBJ = ul.getUser(user);
			if (userOBJ != null)
			{
				if (userOBJ.isDirty())
				{
					String[] reminders = userOBJ.getReminders();
					if (reminders.length > 0)
					{
						for (int i = 0; i < reminders.length;i++)
						{
							irc.sendPrivmsg(channel, reminders[i]);
						}
					}
					else
					{
						irc.sendPrivmsg(channel, user + ": Your host has changed...");
					}
				}
			}
		}
		JSON.saveGSON(cfgFile, rl);
	}

	@Override
	public void onJoin(Join in_join) throws Exception {}
	@Override
	public void onQuit(Quit in_quit) throws Exception {}
	@Override
	public void onKick(Kick in_kick) throws Exception {}
	@Override
	public void onOther(String in_str) throws Exception {}
}
