
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

import org.w3c.dom.ls.LSException;


public class MyPart1 {

	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;
	BufferedImage img;
	int width = 512;
	int height = 512;

	// Draws a black line on the given buffered image from the pixel defined by (x1, y1) to (x2, y2)
	public void drawLine(BufferedImage image, int x1, int y1, int x2, int y2) {
		Graphics2D g = image.createGraphics();
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1));
		g.drawLine(x1, y1, x2, y2);
		g.drawImage(image, 0, 0, null);
	}

	public void showIms(String[] args){

		// Read a parameter from command line
		int n = Integer.parseInt(args[0]);
		Float scale = Float.valueOf(args[1]);
		int bool = Integer.parseInt(args[2]);
		System.out.println("n: " + n);
		System.out.println("Scaling Factor: " + scale);
		System.out.println("Aliasing: " + bool);

		int newWidth = (int)(width*scale);
		int newHeight = (int)(height*scale);
		System.out.println("new size " + newWidth);
		
		// Initialize a plain white image
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage resizedImg = new BufferedImage(newWidth,newHeight , BufferedImage.TYPE_INT_RGB);

		for(int y = 0; y < height; y++){

			for(int x = 0; x < width; x++){

				// byte a = (byte) 255;
				byte r = (byte) 255;
				byte g = (byte) 255;
				byte b = (byte) 255;

				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
				img.setRGB(x,y,pix);
			}
		}

		drawLine(img, 0, 0, width-1, 0);				// top edge
		drawLine(img, 0, 0, 0, height-1);				// left edge
		drawLine(img, 0, height-1, width-1, height-1);	// bottom edge
		drawLine(img, width-1, height-1, width-1, 0); 	// right edge
		
		int centerX = width / 2;
    	int centerY = height/ 2;
    	int radius = Math.min(width, height);

    	double angleIncrement = 360.0 / n;
		for (int i = 0; i < n; i++) {
			double angle = i * angleIncrement;
			int x = centerX + (int) (radius * Math.cos(Math.toRadians(angle)));
			int y = centerY + (int) (radius * Math.sin(Math.toRadians(angle)));
			drawLine(img,centerX, centerY, x, y);
		}

		int newPix,imgX,imgY;

		for (int y=0; y<newHeight;y++){

			for(int x = 0; x < newWidth; x++){

					if(bool == 0){

						newPix = img.getRGB((int)(x/scale),(int)(y/scale));
						resizedImg.setRGB(x, y, newPix);
					}
					else if(bool == 1 ) {

						imgX = (int)(x/scale);
						imgY = (int)(y/scale);

						int r = 0, g = 0, b = 0,count=0;
						for (int i =-1; i<2; i++){
							for (int j=-1; j<2; j++){
								if((imgX+i)>=0 && (imgX+i)<width && (imgY+j)>=0 && (imgY+i)<height){
									newPix = img.getRGB(imgX+i, imgY+j);
									r += (newPix >> 16) & 0xff;
									g += (newPix >> 8) & 0xff;
									b += newPix & 0xff;
									count++;
								}
							}
						}

						if (count > 0){
							r /= count;
							g /= count;
							b /= count;
						}

						newPix = (r << 16) | (g << 8) | b;
						resizedImg.setRGB(x, y, newPix);
				
					}

			}
		}

		drawLine(resizedImg, 0, 0, newWidth-1, 0);				// top edge
		drawLine(resizedImg, 0, 0, 0, newHeight-1);				// left edge
		drawLine(resizedImg, 0, newHeight-1, newWidth-1, newHeight-1);	// bottom edge
		drawLine(resizedImg, newWidth-1, newHeight-1, newWidth-1, 0); 	// right edge

		// Use labels to display the images
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		JLabel lbText1 = new JLabel("Original image (Left)");
		lbText1.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lbText2 = new JLabel("Image after modification (Right)");
		lbText2.setHorizontalAlignment(SwingConstants.CENTER);
		lbIm1 = new JLabel(new ImageIcon(img));
		lbIm2 = new JLabel(new ImageIcon(resizedImg));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(lbText1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		frame.getContentPane().add(lbText2, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		frame.getContentPane().add(lbIm2, c);

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		MyPart1 ren = new MyPart1();
		ren.showIms(args);
	}

}