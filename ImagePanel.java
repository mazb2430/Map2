import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	private ImageIcon image;
	
	public ImagePanel(String fileName) {
		image = new ImageIcon(fileName);
		int w = image.getIconWidth();
		int h = image.getIconHeight();
		Dimension d = new Dimension(w, h);
		setPreferredSize(d);
		setLayout(null);

	}
	
	protected void paintComponent(Graphics g){
		super.paintComponents(g);
		g.drawImage(image.getImage(), 0, 0, this);
	}
}
