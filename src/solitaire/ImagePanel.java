package solitaire;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JFrame;

class ImagePanel extends JComponent {
    private Image image;
    private JFrame frame;
    
    private int imageWidth;
    private int imageHeight;
    
    public ImagePanel(Image image, JFrame frame) {
        this.image = image;
        this.frame = frame;
        
        imageWidth = image.getWidth(this);
        imageHeight = image.getHeight(this);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Rectangle bounds = frame.getBounds();
        int windowWidth = bounds.width;
        int windowHeight = bounds.height;
        
        //round up
        int columns = (windowWidth + imageWidth - 1) / imageWidth;
        int rows = (windowHeight + imageHeight - 1) / imageHeight;
        
        for (int row = 0; row < rows; row++) {
        	for (int col = 0; col < columns; col++) {
        		g.drawImage(image, col * imageWidth, row * imageHeight, this);
        	}
        }
    }
}