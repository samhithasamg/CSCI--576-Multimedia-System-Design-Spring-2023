import java.io.File;
import java.util.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.swing.*;
import java.util.List;

public class  ExtraCredit{

    
	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;
	BufferedImage img;
	int width = 352;
	int height = 288;
	int n;
	int m;

	public void addToMap(Map<int[],List<int[]>> vectors, int[] codevector,int x, int y){
		
		for(int[] array: vectors.keySet()){
			if(Arrays.equals(codevector,array)){
				vectors.get(array).add(new int[]{x,y});
				return;
			}
		}
		vectors.put(codevector,new ArrayList<int[]>(Arrays.asList(new int[]{x,y})));
	}
	public Double calculateDistance(int[] point1, int[] point2){
		 
		Double dist = 0.0;
		for( int i=0; i<point1.length; i++){

			dist+=Math.pow((point1[i]-point2[i]),2);
		}

		return Math.sqrt(dist);
	}

	public int findClosestCentroid(int[] point,List<int[]> centroids){

		Double minDistance = Double.MAX_VALUE;
		int closestCenter= -1;

		for(int l=0; l<n; l++){
			
			Double dist = calculateDistance(centroids.get(l),point);

			if(dist<minDistance){
				
				closestCenter = l;
				minDistance = dist;
			}
		}

		return closestCenter;
	}

	public void calculateNewCentroids(int[] cluster, List<int[]> keysArray,List<int[]> centroids){

		for(int i=0; i< centroids.size();i++){

			int count=0;
			int[] sum= new int[m];
			for(int j=0; j<cluster.length; j++){
				
				if(cluster[j]==i){

					for(int k=0;k<m;k++){
						sum[k]+=keysArray.get(j)[k];
					}

					count++;
				}
			}
			if(count!=0){
				
				for(int k=0;k<m;k++){
					sum[k]= sum[k]/count;
				}
				centroids.set(i,sum);

			}
			else{

				Random random = new Random();
				int[] cen = keysArray.get(random.nextInt(keysArray.size()));
				centroids.set(i,cen);
				
			}
		}

	}

	public List<int[]> KmeansForCompression(Map<int[],List<int[]>> vectors, int[] cluster){

		 //Making n centroids choosing random points
		List<int[]> keysArray = new ArrayList<int[]>(vectors.keySet());
		List<int[]> centroids = new ArrayList<>();
		Random random = new Random();
		for( int i=0; i<n; i++){
			
			centroids.add(keysArray.get(random.nextInt(keysArray.size())));
		}

		boolean stop = false;
		
		int iterations = 0;

		while(!stop){

			iterations++;
			//System.out.println(iterations);
			boolean changeCenter = false;
			for(int i=0; i<keysArray.size();i++){
				
				int center = findClosestCentroid(keysArray.get(i), centroids);
				if(center!=cluster[i]){
					cluster[i]= center;
					changeCenter=true;
				}

			}

			calculateNewCentroids(cluster,keysArray,centroids);

			if(changeCenter==false || iterations>500){
				stop = true;
			}

		}
		
		return centroids;
	}

	public BufferedImage decompressImage(BufferedImage img, int[] cluster, List<int[]> centriods, Map<int[],List<int[]>> vectors){

		BufferedImage deCompressedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
		
		int i=0;
		
		for(int[] key: vectors.keySet()){

			List<int[]> values = vectors.get(key);
			int[] pixels = centriods.get(cluster[i]);
			for(int[] value: values){

				byte r,g,b;
				int ind=0;
				for(int j=0;j<Math.sqrt(m);j++){

					for(int k=0; k<Math.sqrt(m);k++){

						r = (byte)pixels[ind];
						g=r; b=r;
						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
						deCompressedImg.setRGB(value[0]+k,value[1]+j,pix);

						ind++;
					}

				}
			}

			i++;
			
		}

		return deCompressedImg;

	}
    public void showImg(String[] args) throws Exception{

        String filenameString= args[0];
        m = Integer.parseInt(args[1]);
        n = Integer.parseInt(args[2]);

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
				
				img.setRGB(x,y,pix);
                ind++;
			}
		}

		Map<int[],List<int[]>> vectors =  new HashMap<int[], List<int[]>>();

       
        for(int y = 0; y < height-Math.sqrt(m); y+=Math.sqrt(m)){

            for(int x = 0; x < width-Math.sqrt(m); x+=Math.sqrt(m)){
				int r;
				int[] codevector = new int[m];
				ind=0;
				for( int i =0; i<Math.sqrt(m); i++){
					for (int j=0; j<Math.sqrt(m); j++){
						int pix = img.getRGB(x+j, y+i);
						r = (pix >> 16) & 0xff;
						codevector[ind++]=r;
					}
				}

				addToMap(vectors,codevector,x,y);
		
            }
        }
		System.out.println(vectors.size());
		
		int[] cluster = new int[vectors.size()];
		List<int[]> centroids = KmeansForCompression(vectors,cluster);


		BufferedImage decompressedImg; 
      
		decompressedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		decompressedImg = decompressImage(img,cluster,centroids,vectors);
		
		// Use labels to display the images
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		JLabel lbText1 = new JLabel("Original image (Left)");
		lbText1.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lbText2 = new JLabel("Image after modification (Right)");
		lbText2.setHorizontalAlignment(SwingConstants.CENTER);
		lbIm1 = new JLabel(new ImageIcon(img));
		lbIm2 = new JLabel(new ImageIcon(decompressedImg));

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
    //FileWriter writer = new FileWraiter("./image1-onechannel.rgb");

		ExtraCredit res = new ExtraCredit();
        res.showImg(args);

    }
    
}
