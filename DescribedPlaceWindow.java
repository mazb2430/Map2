import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JComponent;

public class DescribedPlaceWindow extends JComponent{
	DescribedPlace dp;
	
	public DescribedPlaceWindow(Place p) {
		this.dp = (DescribedPlace) p;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponents(g);
		
		g.setColor(Color.YELLOW);
		g.fillRect(dp.getX(), dp.getY()+48, 225, 50);
		g.setColor(Color.BLACK);
		g.setFont(new Font("arial", Font.BOLD, 12));
		g.drawString(String.format("%s \n %s",dp.name, dp.description), dp.getX(), dp.getY()+82);
		
	}
	
}
