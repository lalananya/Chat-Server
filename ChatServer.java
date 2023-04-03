import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileSystemView;

public class ChatServer implements ActionListener{

	JFrame frame;
	JButton login;
	JTextArea tf;
	JLabel label;
	JPanel panel;
	String name="";
	public ChatServer(String s)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			frame=new JFrame(s);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			panel=new JPanel();
			panel.setBackground(Color.LIGHT_GRAY);
			panel.setLayout(new BorderLayout());
			
			label=new JLabel("CHAT SERVER");
			label.setForeground(Color.BLACK);
			panel.add(label,BorderLayout.NORTH);
			
			tf=new JTextArea(5,5);
			panel.add(tf,BorderLayout.CENTER);
			
			login=new JButton("LOGIN");
			panel.add(login,BorderLayout.SOUTH);
			login.addActionListener(this);
			
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation(dim.width/4-frame.getSize().width/4, dim.height/4-frame.getSize().height/4);
			
			frame.add(panel,"Center");
			frame.setSize(300,500);
			frame.setVisible(true);	
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	public static void main(String[] args){
         new ChatServer("Chat Server");
	}
	@Override
	public void actionPerformed(ActionEvent e)
	  {
       name=tf.getText();
       if(e.getSource()==login)
       {
		if(name.equals(""))
		    {
			 JOptionPane.showMessageDialog(null,"Please Enter Your Name");
		    }
	       else
		    {
		       frame.setVisible(false);
			   new Client(name);
		    }
		}
     }
}

class Client extends JFrame implements ActionListener,KeyListener
{
	private static final long serialVersionUID = 1L;
	Socket s;
	DataInputStream din;
	DataOutputStream dout;
	JButton logout,clear,getusers;
	JPanel panelsouth,panelcenter;
	JTextField textfield;
	JTextArea displayarea,connectedarea;
	String message="",name="";
	JScrollPane scrollpane,scrollpane2;
	JMenu menu;
	JMenuBar menubar;
	JMenuItem menuitem;
	Client(String name)
	{

		try
		{
			this.name=name;
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());		
			this.setSize(600,600);
			this.setResizable(true);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setTitle("Connected as : " + name);
			menubar=new JMenuBar();
			menu=getFileMenu();
			menubar.add(menu);
			this.setJMenuBar(menubar);
			
			panelcenter=new JPanel();
			panelcenter.setLayout(new FlowLayout());
			panelcenter.setBackground(Color.WHITE);
			panelcenter.setBorder(new TitledBorder("Press Enter to send message"));
			
			displayarea=new JTextArea(24,30);
			displayarea.setEditable(false);
			scrollpane=new JScrollPane(displayarea);
			panelcenter.add(scrollpane);
			displayarea.setFont(new Font("Arial",Font.PLAIN,16));
			
			connectedarea=new JTextArea(24,10);
			connectedarea.setEditable(false);
			scrollpane2=new JScrollPane(connectedarea);
			panelcenter.add(scrollpane2);
			connectedarea.setFont(new Font("Arial",Font.PLAIN,16));
			
			panelsouth=new JPanel();
			panelsouth.setLayout(new FlowLayout());
			panelsouth.setBorder(new TitledBorder(new EtchedBorder()));
			
			textfield=new JTextField(25);
			textfield.addKeyListener(this);
			textfield.setFont(new Font("Arial",Font.PLAIN,16));
			panelsouth.add(textfield);
			
			clear=new JButton("CLEAR");
			clear.addActionListener(this);
			panelsouth.add(clear);
			
			logout=new JButton("LOGOUT");
			logout.addActionListener(this);
			panelsouth.add(logout);
			
			getusers=new JButton("GET USERS");
			getusers.addActionListener(this);
			panelsouth.add(getusers);
			
			this.add(panelsouth,"South");
			this.add(panelcenter,"Center");
			this.setVisible(true);
			textfield.requestFocus();
			displayarea.requestFocus();
			
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
			
			s=new Socket("localhost",2048);
			din=new DataInputStream(s.getInputStream());// to receive
			dout= new DataOutputStream(s.getOutputStream());// to send
			MyRun m = new MyRun(this,din);
			Thread t1 = new Thread(m);
			t1.start();
			dout.writeUTF(name+"`");
		}catch(Exception e){System.err.println(e);}	
	}
	JMenu getFileMenu()
	{
		menu=new JMenu("File");
		menuitem=new JMenuItem("Choose File");
		menuitem.addActionListener(this);
		menu.add(menuitem);
		return menu;
	}

	public void actionPerformed(ActionEvent e)
	{    
		if(e.getSource()==clear)
		{
			textfield.setText("");
		}
		
		if(e.getSource()==logout)
		{
			try {
				dout.writeUTF(name + " " + "Logged Out");
			} catch (IOException e1) { e1.printStackTrace();}
			this.dispose();
		}
		
		if(e.getSource()==getusers)
		{
			try 
			{
				dout.writeUTF("121");
				dout.flush();
			} catch (IOException e1) { System.err.println(e1);}	 
		
		}
		if(e.getActionCommand()=="Choose File")
		{
			choose();
		}
	}

	void choose() {
		
		JFileChooser choosefile = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		choosefile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
	}
	@Override
	public void keyPressed(KeyEvent ev) {
		if(ev.getKeyCode()==KeyEvent.VK_ENTER)
		{
			try
			{
				message=textfield.getText();
				message=name+": "+ message;
				dout.writeUTF(message); 
				dout.flush();
			}catch(Exception exp)
			{
				exp.printStackTrace();
			}
			textfield.setText("");
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}

}


class MyRun implements Runnable
{
	Client mychat;
	DataInputStream din;
	String name;
	MyRun(Client mychat,DataInputStream din)
	{
		this.din = din;
		this.mychat=mychat;
	}
	public void run()
	{
		String s2="";
		do
		{
			try
			{
				s2=din.readUTF(); //server se wapas aaya!
				if(s2.charAt(s2.length()-1)=='`')
				{
					mychat.connectedarea.setText(s2);
				}
				else
				{	
				    mychat.displayarea.append(s2 +"\n");
				}
			} catch(Exception e) {}
		}while(!s2.equals("logoutlogoutlogoutlogoutlogout1234567asdfghre@#$%^&"));
	}
}

