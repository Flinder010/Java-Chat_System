package Chat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server extends JFrame implements ActionListener {
	public static void main( String [] args ) throws Exception{
		
		new Server();
		
	}
	private JTextArea jta;  // 文本域
	private JScrollPane jsp;  // 滚动条
	private JPanel jp;  // 画板
	private JTextField jtf;  // 文本框
	private JButton jb;  // 按钮
	
	private BufferedWriter bfrw = null;  
	
	public Server(){
		jta = new JTextArea();
		jta.setEditable( false );
		jsp = new JScrollPane( jta );
		jp = new JPanel();
		jtf = new JTextField( 10 );
		jb = new JButton( "发送" );
		
		jp.add( jtf );
		jp.add( jb );
		
		this.add( jsp , BorderLayout.CENTER );
		this.add( jp , BorderLayout.SOUTH );
		
		this.setTitle( "点对点通信聊天（服务器）" );
		this.setSize( 450 , 450 );
		this.setLocation( 600 , 300 );
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setVisible( true );
		
		jb.addActionListener( this );

		try{
			ServerSocket ss = new ServerSocket( 10001 );
			Socket sk = ss.accept();
			System.out.println( "有客户端连接" );
			BufferedReader bfrConsle = new BufferedReader( new InputStreamReader( System.in ) );
			InputStream ios = sk.getInputStream();
			BufferedReader bfrNet = new BufferedReader( new InputStreamReader( ios ) );
			OutputStream os = sk.getOutputStream();
			bfrw = new BufferedWriter( new OutputStreamWriter( os ) );
			PrintWriter pwNet = new PrintWriter( new OutputStreamWriter( os ) );
			new Thread( new ServerThread( sk , jta ) ).start(); // 加入多线程
			String line = null;
			while( true ){
				line = bfrNet.readLine();
				String str = line;
				if( !str.equals( "exit" ) ){
					jta.append( line + System.lineSeparator() );
				}
				/*str = bfrConsle.readLine();
				pwNet.flush();
				if( str.equals( "exit" ) ) break;
				pwNet.println(str);*/
				// 将上述操作放到监听器中实现
				if( str.equals( "exit" ) ) break;
				System.out.println( str );
			}
			if( !sk.isClosed() ){
				sk.close();
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
		this.setVisible( false );
		System.exit( 0 );
	}
	
	// 监听器实现
	public void actionPerformed( ActionEvent e ){
		//System.out.println("发送");
		String text = jtf.getText();
		if( !text.equals( "exit" ) ) text = "服务器对客户端说：" + text;
		if( !text.equals( "exit" ) ) jta.append( text + System.lineSeparator() );
		try{
			bfrw.write( text );
			bfrw.newLine();
			bfrw.flush();
			jtf.setText( "" );  // 实现文本的清空
		}catch( Exception e1 ){
			e1.printStackTrace();
		}
	}
}

// 实现多线程
class ServerThread implements Runnable{
	private Socket sk;
	private JTextArea jta;
	public ServerThread( Socket sk , JTextArea jta ){
		this.sk = sk;
		this.jta = jta;
	}
	public void run(){
		byte[] bytes = new byte[1024];
		int len;
		try{
			InputStream inputStream = sk.getInputStream();
			while( true ){
				len = inputStream.read( bytes );
				String str1 = new String( bytes , 0 , len );
				jta.append( str1 );
				System.out.print( new String( bytes , 0 , len ) ); // 用字符串方法实现读写
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
	}
}
// Copyright@Flinder