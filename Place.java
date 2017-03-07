import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.event.*;

abstract public class Place extends JComponent implements Serializable {
	private static final long serialVersionUID = 7312739738890456127L;
	protected String name;
	protected Position pos;
	protected Color color;
	protected Category category; 
	protected boolean folded = false;
	protected boolean marked = false;
	protected boolean hidden = false; 

	public Place(String name, int x, int y, Color color, Category category) {
		this.name = name;
		this.pos = new Position(x, y);
		this.color = color;
		this.category = category;
		setBounds(pos.getX(), pos.getY(), 22, 22);
		Dimension d = new Dimension(22, 22);
		setPreferredSize(d);
		setMaximumSize(d);
		setMinimumSize(d);
	}

	abstract protected void draw(Graphics g);
	abstract public void hide(Graphics g);

	protected void paintComponent(Graphics g) {
		super.paintComponents(g);

		if (!folded) {
			draw(g);
		}
		else if (folded){
			fold(g);
		}

		if (marked) {
			g.setColor(Color.RED);
			g.drawRect(0, 0, getWidth()-1, getHeight()-1);

		}
	}

	public void fold(Graphics g) {

		if (this instanceof NamedPlace) {

			g.setColor(Color.RED);
			g.drawRect(0, 0, getWidth()-10, getHeight()-10);
			g.setFont(new Font("arial", Font.BOLD, 14));
			g.drawString("Place: " + name, 1, 15);

		} else if (this instanceof DescribedPlace) {
			DescribedPlace d = (DescribedPlace) this;

			g.setColor(Color.YELLOW);
			g.fillRect(0, 0, getWidth()-10, getHeight()-10);
			g.setColor(Color.BLACK);
			g.setFont(new Font("arial", Font.BOLD, 12));
			g.drawString("Place: " + name, 1, 15);
			g.drawString("Description: " + d.description, 1, 30);

		}
	}

	class MouseLis implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			System.out.println("Young thug");
		}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}

		public void mousePressed(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) {}

	}

	public boolean getFolded() {
		return folded;
	}

	public void setFolded() {

		if(this instanceof NamedPlace) {
			if (folded == false) {
				folded = true;
				setBounds(getX(), getY(), 150, 50);
			} else {
				folded = false;
				setBounds(getX(), getY(), 22, 22);
			}
		} else if(this instanceof DescribedPlace) {
			if (folded == false) {
				folded = true;
				setBounds(getX(), getY(), 226, 51);
			} else {
				folded = false;
				setBounds(getX(), getY(), 22, 22);
			}
		}
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Position getPos() {
		return pos;
	}

	public void setPos(Position pos) {
		this.pos = pos;
	}

	public Place getPlace() {
		return this;
	}

	public Category getCategory() {
		return category;
	}

	public void found() {
		this.setVisible(true);
	}

	protected boolean getMarked() {
		return this.marked;
	}

	protected boolean getHidden() {
		return this.hidden;
	}

	abstract public String toString();

}

