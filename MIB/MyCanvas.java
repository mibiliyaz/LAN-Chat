import java.awt.*;
import javax.swing.JFrame;

public class MyCanvas extends Canvas{
	
	public void paint(Graphics g) {
		Toolkit t=Toolkit.getDefaultToolkit();
		// Image i=t.getImage("p3.gif");
        Image i=t.getImage("MIBLogo.jpg");
		g.drawImage(i, 5,6,this);
			
			
	}
		public static void main(String[] args) {
		MyCanvas m=new MyCanvas();
		JFrame f=new JFrame();
		f.add(m);
		f.setSize(400,400);
		//f.setLayout(null);
		f.setVisible(true);
	}

}
