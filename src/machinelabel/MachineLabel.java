/*

    Copyright (c) 2015 Andy Gock

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.

 */

package machinelabel;

import java.io.IOException;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;

/**
 *
 * @author andy
 */
public class MachineLabel {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        if (args.length != 2 && args.length != 3) {
            //System.err.println("java " + MachineLabel.class.getSimpleName() + " MACHINE_NAME MAC_ADDRESS IP_ADDRESS");
            System.err.println("java -jar " + MachineLabel.class.getSimpleName() + ".jar MACHINE_NAME MAC_ADDRESS [IP_ADDRESS]");
            System.exit(1);
        }
        
        String machineName = args[0];
        String macAddress = args[1];
        
        String ipAddress;
        if (args.length == 3) {
            ipAddress = args[2];
        } else {
            ipAddress = null;
        }

        // allow mac address with either '-' or ':' delimiters
        // but we want to convert them to ':' from now on
        macAddress = macAddress.replaceAll("-", ":");
        
        try {

            // Make the barcodes and write them to disk as PNGs (3x files)
            Label label = new Label(machineName, macAddress, ipAddress);
            label.writePNG();
            
            // Concatenate the PNGs
            String[] imageNames = new String[] {
                "machine_name.png",
                "mac_address.png",
                "ip_address.png"
            };
            stackImages(machineName + ".png", imageNames);
            
            // Delete the source PNGs
            for (String filename: imageNames) {
                File f = new File(filename);
                f.delete();
            }
            
            System.exit(0);
            
        } catch (BarcodeException | OutputException | IOException e) {
            System.err.println("Exception occured:");
            e.printStackTrace();
            System.exit(1);
        }

    }
    
    private static String[] arrayJoin(String[] a, String[] b) {
        String[] output = new String[a.length + b.length];
        System.arraycopy(a,0,output,0,a.length);
        System.arraycopy(b,0,output,a.length,b.length);
        return output;
    }
    
    // Stack/append images using native java methods
    private static void stackImages(String outputFileName, String[] imageFileName) throws IOException {
        //System.out.println(System.getProperty("user.dir"));

        // space between stacking
        int stackHeight = 15;
        
        int srcWidth;
        int srcHeight;
        int totalHeight = 0;
        int maxWidth = 0;
        
        // load individual source images
        BufferedImage[] buffImage = new BufferedImage[imageFileName.length];
        for (int n = 0; n < imageFileName.length; n++) {
            buffImage[n] = ImageIO.read(new File(imageFileName[n]));
            
            srcWidth = buffImage[n].getWidth();
            srcHeight = buffImage[n].getHeight();
            
            // keep track of max width
            if (srcWidth > maxWidth) {
                maxWidth = srcWidth;
            }

            totalHeight += (srcHeight + stackHeight);
        }
        
        totalHeight += stackHeight;
        
        // create final image
        int canvasWidth = maxWidth + 2 * stackHeight;
        int canvasHeight = totalHeight;
        BufferedImage finalImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_BYTE_INDEXED);
        Graphics2D graphics = finalImage.createGraphics();
        
        // fill with white
        graphics.fill(new Rectangle(canvasWidth, canvasHeight));
        
        // stack the images
        int y = stackHeight;
        for (int n = 0; n < imageFileName.length; n++) {
            // Notes about drawImage()
            //   param 2 onwards = x, y, width, height
            //   x, y origin is top left of canvas
            
            int imageWidth = buffImage[n].getWidth();
            int imageHeight = buffImage[n].getHeight();
            
            // calculate x position for centering
            int x_centered = (canvasWidth - imageWidth) / 2;
            
            // draw the image onto the canvas
            graphics.drawImage(buffImage[n], x_centered, y, imageWidth, imageHeight, null);
            
            // calculate y position for next iteration
            y += imageHeight;
            y += stackHeight;
        }
        
        ImageIO.write(finalImage,"png",new File(outputFileName));
    }
    
}
