//Only incorporated the spatial aliasing part in the video.
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.lang.Math.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.ls.LSException;

import java.util.Timer;
import java.util.TimerTask;

import java.time.LocalTime;


public class MyExtraCredit{

	static JFrame frameLeft;
    static JFrame frameRight;
	static JLabel lbIm1;
	static JLabel lbIm2;
	static BufferedImage img;
	static BufferedImage opImg;
	static int width = 512;
	static int height = 512;
	static double angleStepL = 0;
    static int n;
    static Float rps;
    static int fps;
	static int alias;
	static Float scale;
    static Container cLeft;
    static Container cRight;


	// Draws a black line on the given buffered image from the pixel defined by (x1, y1) to (x2, y2)
	public static void drawLine(BufferedImage image, int x1, int y1, int x2, int y2) {
		Graphics2D g = image.createGraphics();
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1));
		g.drawLine(x1, y1, x2, y2);
		g.drawImage(image, 0, 0, null);
	}

    public static Runnable createMain() {
        return () -> {
			for(int y = 0; y < height; y++){

				for(int x = 0; x < width; x++){

					// byte a = (byte) 255;
					byte r = (byte) 255;
					byte g = (byte) 255;
					byte b = (byte) 255;

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
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
				double angle = i * angleIncrement + angleStepL;
				int x = centerX + (int) (radius * Math.cos(Math.toRadians(angle)));
				int y = centerY + (int) (radius * Math.sin(Math.toRadians(angle)));
				drawLine(img,centerX, centerY, x, y);
			}

			angleStepL+=360*rps/60;

            lbIm1 = new JLabel(); //JLabel Creation
            lbIm1.setIcon(new ImageIcon(img)); //Sets the image to be displayed as an icon
            cLeft.add(lbIm1);
			
            //frameLeft.repaint();
            frameLeft.pack();
			frameLeft.setVisible(true);		

        };
			

    }

    public static Runnable createOp() {

       return () -> { 
		
		BufferedImage copy= new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		copy=img;

		int newWidth = (int)(width/scale) ;
		int newHeight = (int)(height/scale);

		int newPix, imgX, imgY;
		//System.out.print("there");
		if(alias == 0 ){
			for (int y=0; y<newHeight;y++){
				for(int x = 0; x < newWidth; x++){
					newPix = copy.getRGB((int)(x*scale),(int)(y*scale));
					opImg.setRGB(x, y, newPix);
				}	
			}
	
		}
		else{
			//System.out.print("here");
			for (int y=0; y<(int)(height/scale);y++){
				for(int x = 0; x < (int)(width/scale); x++){
					
					imgX = (int)(x*scale);
					imgY = (int)(y*scale);

					int r = 0, g = 0, b = 0,count=0;
					for (int i =-1; i<2; i++){
						for (int j=-1; j<2; j++){
							if((imgX+i)>=0 && (imgX+i)<width && (imgY+j)>=0 && (imgY+i)<height){
								newPix = copy.getRGB(imgX+i, imgY+j);
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
					opImg.setRGB(x, y, newPix);
			
				}
			}
	
		}
		
		//opImg=img;
       
		lbIm2 = new JLabel(); //JLabel Creation
		lbIm2.setIcon(new ImageIcon(opImg)); //Sets the image to be displayed as an icon
		cRight.add(lbIm2);

        frameRight.pack();
		frameRight.setVisible(true);	
       };
        
    }

	public void showIms(String[] args){

		// Read a parameter from command line
		n = Integer.parseInt(args[0]);
		rps = Float.valueOf(args[1]);
		fps = Integer.parseInt(args[2]);
		alias = Integer.parseInt(args[3]);
		scale = Float.valueOf(args[4]);
		System.out.println("The first parameter was: " + n);
		System.out.println("The second parameter was: " + rps);

		// Initialize a plain white image
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		opImg = new BufferedImage((int)(width/scale),(int) (height/scale), BufferedImage.TYPE_INT_RGB);		

		
		frameLeft = new JFrame();
        frameRight = new JFrame();

        frameLeft.setTitle("Original Video (Left)");
        frameRight.setTitle("Video after modification (Right)");
		
        cLeft = frameLeft.getContentPane();
        cRight = frameRight.getContentPane();

		
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

        scheduledExecutorService.scheduleAtFixedRate(createMain(), 0, 1000*1000000/60, TimeUnit.NANOSECONDS);
        scheduledExecutorService.scheduleAtFixedRate(createOp(), 0, 1000*1000000/fps, TimeUnit.NANOSECONDS);

	 }

	public static void main(String[] args) throws InterruptedException { 
		MyExtraCredit ren = new MyExtraCredit();
		ren.showIms(args);
	}

}
