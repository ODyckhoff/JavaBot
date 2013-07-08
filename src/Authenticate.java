import java.security.MessageDigest;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import addons.AuthenticatedUsers;
import addons.User;
import addons.UserList;
import event.Join;
import event.Kick;
import event.Message;
import event.Quit;
import plugin.PluginTemp;
import program.Details;
import program.IRC;


public class Authenticate implements PluginTemp
{
	AuthenticatedUsers auth_Users = AuthenticatedUsers.getInstance();
	
	@Override
	public String name() 
	{
		return "Authentication";
	}

	@Override
	public void onTime() throws Exception 
	{
		
	}

	@Override
	public void onMessage(Message in_message) throws Exception 
	{
		IRC irc = IRC.getInstance();
		
		if (in_message.isPrivMsg())
		{
			User user = UserList.getInstance().getUser(in_message.getUser());
			if (user != null)
			{
				if (in_message.getMessage().matches("LOGIN .*"))
				{
					if (!auth_Users.contains(user))
					{
						if (user.getEmail() == null||user.getEmail().isEmpty())
						{
							irc.sendPrivmsg(in_message.getChannel(), "Please Register");
						}
						else
						{
							Matcher m = Pattern.compile("LOGIN (.*@.*\\..*) (.*)",
									Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
									.matcher(in_message.getMessage());
							if (m.find())
							{
								String email = m.group(1), password = m.group(2);
								
								byte[] rawHaPwd = hashPassword(password);
								
								byte[] enHaPsw = user.getEncryptedPasswordHash();
								
								if (Arrays.equals(rawHaPwd, decrpytPasswordHash(enHaPsw))
										&& user.getEmail().equals(email))
								{
									irc.sendPrivmsg(in_message.getChannel(), "Authenticated");
									auth_Users.add(user);
								}
								else
								{
									irc.sendPrivmsg(in_message.getChannel(), "Incorrect login details");
								}
							}
							else
							{
								irc.sendPrivmsg(in_message.getChannel(), "LOGIN (EMAIL) (PASSWORD)");
							}
						}
					}
					else
					{
						irc.sendPrivmsg(in_message.getChannel(), "Already Logged in");
					}
				}
				else if (in_message.getMessage().matches("REGISTER .*"))
				{
					Matcher m = Pattern.compile("REGISTER (.*@.*\\..*) (.*)",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
							.matcher(in_message.getMessage());
					if (m.find())
					{
						if (user.getEmail() == null||user.getEmail().isEmpty())
						{
							String email = m.group(1), password = m.group(2);
							byte[] hashedPassword = hashPassword(password);
							byte[] enPassword = encryptPasswordHash(hashedPassword);
							user.setEncyptedPasswordHash(enPassword);
							user.setEmail(email);
							irc.sendPrivmsg(in_message.getChannel(), "You are now Registered");
						}
						else
						{
							irc.sendPrivmsg(in_message.getChannel(), "You are already registered");
						}
					}
					else
					{
						irc.sendPrivmsg(in_message.getChannel(), "REGISTER (EMAIL) (PASSWORD)");
					}
				}
				else if (in_message.getMessage().matches("LOGOUT"))
				{
					if (auth_Users.contains(user))
					{
						auth_Users.remove(user);
						irc.sendPrivmsg(in_message.getChannel(), "You have been logged out!");
					}
					else
					{
						irc.sendPrivmsg(in_message.getChannel(), "You were not logged in");
					}
				}
				else if (in_message.getMessage().matches("RECOVER .*"))
				{
					Matcher m = Pattern.compile("RECOVER (.*@.*\\..*)",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
							.matcher(in_message.getMessage());
					if (m.find())
					{
						//Implement email first
					}
					else
					{
						
					}	
				}
			}
		}
	}

	@Override
	public void onJoin(Join in_join) throws Exception 
	{
		IRC irc = IRC.getInstance();
		if (!in_join.getUser().equals(Details.getIntance().getNickName().toLowerCase()))
		{
			User user = UserList.getInstance().getUser(in_join.getUser());
			if (!auth_Users.contains(user))
				if (user.getEmail() != null&&!user.getEmail().isEmpty())
					irc.sendPrivmsg(user.getUser(), "Please Login!");
		}
	}

	@Override
	public void onQuit(Quit in_quit) throws Exception
	{
		User user = UserList.getInstance().getUser(in_quit.getUser());
		auth_Users.remove(user);
	}

	@Override
	public void onKick(Kick in_kick) throws Exception
	{
		User user = UserList.getInstance().getUser(in_kick.getKicked());
		auth_Users.remove(user);
	}
	
	
	private byte[] hashPassword(String password) throws Exception
	{
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(password.getBytes());
		return new String(messageDigest.digest()).getBytes();
	}
	
	private byte[] encryptPasswordHash(byte[] paswordHash) throws Exception
	{
		byte[] encryptkey = Details.getIntance().getEncryptionKey();
		SecretKey sKey = new SecretKeySpec(encryptkey,0,encryptkey.length, "AES");
		
		Cipher desCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	    
	    	desCipher.init(Cipher.ENCRYPT_MODE, sKey);
	    
	   	return desCipher.doFinal(paswordHash);
	}

	private byte[] decrpytPasswordHash(byte[] enPasswordHash) throws Exception
	{
		byte[] decryptkey = Details.getIntance().getEncryptionKey();
		SecretKey sKey = new SecretKeySpec(decryptkey,0,decryptkey.length, "AES");
		
		Cipher desCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	    
		desCipher.init(Cipher.DECRYPT_MODE, sKey);
		return desCipher.doFinal(enPasswordHash);
	}
	
	@Override
	public void onCreate() throws Exception {}
	@Override
	public void onOther(String in_str) throws Exception {}
}
