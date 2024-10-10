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
	private JTextArea jta;  // �ı���
	private JScrollPane jsp;  // ������
	private JPanel jp;  // ����
	private JTextField jtf;  // �ı���
	private JButton jb;  // ��ť
	
	private BufferedWriter bfrw = null;  
	
	public Server(){
		jta = new JTextArea();
		jta.setEditable( false );
		jsp = new JScrollPane( jta );
		jp = new JPanel();
		jtf = new JTextField( 10 );
		jb = new JButton( "����" );
		
		jp.add( jtf );
		jp.add( jb );
		
		this.add( jsp , BorderLayout.CENTER );
		this.add( jp , BorderLayout.SOUTH );
		
		this.setTitle( "��Ե�ͨ�����죨��������" );
		this.setSize( 450 , 450 );
		this.setLocation( 600 , 300 );
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setVisible( true );
		
		jb.addActionListener( this );

		try{
			ServerSocket ss = new ServerSocket( 10001 );
			Socket sk = ss.accept();
			System.out.println( "�пͻ�������" );
			BufferedReader bfrConsle = new BufferedReader( new InputStreamReader( System.in ) );
			InputStream ios = sk.getInputStream();
			BufferedReader bfrNet = new BufferedReader( new InputStreamReader( ios ) );
			OutputStream os = sk.getOutputStream();
			bfrw = new BufferedWriter( new OutputStreamWriter( os ) );
			PrintWriter pwNet = new PrintWriter( new OutputStreamWriter( os ) );
			new Thread( new ServerThread( sk , jta ) ).start(); // ������߳�
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
				// �����������ŵ���������ʵ��
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
	
	// ������ʵ��
	public void actionPerformed( ActionEvent e ){
		//System.out.println("����");
		String text = jtf.getText();
		if( !text.equals( "exit" ) ) text = "�������Կͻ���˵��" + text;
		if( !text.equals( "exit" ) ) jta.append( text + System.lineSeparator() );
		try{
			bfrw.write( text );
			bfrw.newLine();
			bfrw.flush();
			jtf.setText( "" );  // ʵ���ı������
		}catch( Exception e1 ){
			e1.printStackTrace();
		}
	}
}

// ʵ�ֶ��߳�
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
				System.out.print( new String( bytes , 0 , len ) ); // ���ַ�������ʵ�ֶ�д
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
	}
}
// Copyright@Flinder