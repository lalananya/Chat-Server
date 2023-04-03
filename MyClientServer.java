import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class MyClientServer
{
    ArrayList al = new ArrayList();
    static List<String> users=new ArrayList<String>();
    DataInputStream din;
	ServerSocket ss;
	Socket s;
	public MyClientServer()
	{
		try
		{
			ss = new ServerSocket(2048);
			while(true)
			 {
				s = ss.accept();
				System.out.println("Client connected from " + s.getLocalAddress().getHostName());
				System.out.println("Connected");
				al.add(s); // adding the client socket to array list
				Runnable r = new MyServerThread(s,al);
				Thread t = new Thread(r);
				t.start();
			 }
		} catch(Exception e) { System.err.println(e);}
	}
	public static void main(String... s)
	{
		new MyClientServer();
	}
	
}

class MyServerThread implements Runnable
{
	Socket s;
	ArrayList al;
	
	MyServerThread(Socket s,ArrayList al)
	{
		this.s = s;
		this.al = al;
	}
	public void run()
	{
		String s1="";
		try
		{
			DataInputStream din = new DataInputStream(s.getInputStream());
			do
			{
				s1 = din.readUTF();
				if(s1!="logoutlogoutlogoutlogoutlogout1234567asdfghre@#$%^&")
				{
				   if(s1.charAt(s1.length()-1)=='`')
				    {
				      MyClientServer.users.add(s1);
				    }
				   else if(s1=="121")
				   {
				    getOnlineUsers();
				   }
				   else
				    {
				    tellEveryOne(s1);
				    }
				}
				else
				{
					DataOutputStream dout = new DataOutputStream(s.getOutputStream());
					al.remove(s);
				}
			}while(!s1.equals("logoutlogoutlogoutlogoutlogout1234567asdfghre@#$%^&"));
		} catch(Exception e) {}
	}
	public void getOnlineUsers() // not working
	{
		String s="";
		String[] getusers=new String[MyClientServer.users.size()];
		getusers=MyClientServer.users.toArray(getusers);
		for(String z:getusers)
		{
			System.out.println(z);
		}
		Iterator i=al.iterator();
		while(i.hasNext())
		{
			try
			{
		    Socket sc = (Socket)i.next();
			DataOutputStream dout=new DataOutputStream(sc.getOutputStream());
			for(String z:getusers)
			{
			 s=s+z+"\n";
			}
			dout.writeUTF(s+"`");
			dout.flush();
			}catch(Exception e){}
		}
		
	}
	public void tellEveryOne(String s)
	{
		Iterator i = al.iterator();
		while(i.hasNext())
		{
			try
			{
				Socket sc = (Socket)i.next();
				DataOutputStream dout = new DataOutputStream(sc.getOutputStream());
				dout.writeUTF(s);
				dout.flush();
			} catch(Exception e) {}
		}
	}
}
