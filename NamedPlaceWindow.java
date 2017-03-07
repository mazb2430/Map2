import java.awt.*;

import javax.swing.*;

public class NamedPlaceWindow extends JComponent {
	Place p;
	
	public NamedPlaceWindow(Place p) {
		this.p = p;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponents(g);
		
		g.setColor(Color.RED);
		g.setFont(new Font("arial", Font.BOLD, 14));
		g.drawRect(p.getX()+22, p.getY()+57, 100, 50);
		g.drawString(p.name, p.getX()+25, p.getY()+90);
		revalidate();
		repaint();
		
	}
	
}
