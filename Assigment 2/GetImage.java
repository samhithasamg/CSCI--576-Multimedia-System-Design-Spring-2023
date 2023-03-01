import java.io.File;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class  GetImage{

    
	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;
	BufferedImage img;
	int width = 352;
	int height = 288;


    public void showImg(String[] args) throws Exception{

        String filenameString= args[0];
        int m = Integer.parseInt(args[1]);
        int n = Integer.parseInt(args[2]);

        System.out.println("file: "+filenameString);
        System.out.println("m: "+m);
        System.out.println("n: "+n);

        byte[] imageData = java.nio.file.Files.readAllBytes(new File(filenameString).toPath());
        BufferedImage img; 
      
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int ind=0;
		for(int y = 0; y < height; y++){

			for(int x = 0; x < width; x++){

				// byte a = (byte) 255;
				byte r = imageData[ind];
				byte g = imageData[ind];
				byte b = imageData[ind];

				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
				img.setRGB(x,y,pix);
                ind++;
			}
		}



		// Use labels to display the images
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		JLabel lbText1 = new JLabel("Original image (Left)");
		lbText1.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lbText2 = new JLabel("Image after modification (Right)");
		lbText2.setHorizontalAlignment(SwingConstants.CENTER);
		lbIm1 = new JLabel(new ImageIcon(img));
		lbIm2 = new JLabel(new ImageIcon(img));

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

    public static void main(String[] args) throws Exception {
    //FileWriter writer = new FileWriter("./image1-onechannel.rgb");

        GetImage res = new GetImage();
        res.showImg(args);
    }
    
}
