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
import org.kohsuke.args4j.*;

/**
 *
 * @author andy
 */
public class MachineLabel {
    
    @Option(name = "-w", usage = "Bar width", metaVar = "WIDTH")
    private int barWidth = 3;
    
    @Option(name = "-h", usage = "Bar height", metaVar = "HEIGHT")
    private int barHeight = 70;
    
    @Option(name = "-s", usage = "Spacing", metaVar = "SPACE")
    private int spacing = 15;
    
    @Option(name = "-o", usage = "Output file name", metaVar = "FILENAME")
    private String outputFilename = "output.png";
    
    @Argument(usage = "Data fields", required = true, metaVar = "DATAx")
    private String[] data;
    
    public void run(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            // read command line arguments
            parser.parseArgument(args);
            
            if (data.length == 0) {
                // parseArgument should already take care of this
                System.exit(1);
            }
            
            // create the PNGs
            for (String d: data) {
                Label label = new Label(d);
                label.setBarHeight(barHeight);
                label.setBarWidth(barWidth);
                label.writePNG(d + ".png");
            }
            
            // create array of image filenames
            String[] imageFilenames = new String[data.length];
            for (int n = 0; n < data.length; n++) {
                imageFilenames[n] = data[n] + ".png";
            }
            
            // stack the images
            stackImages(outputFilename, imageFilenames, spacing);

            // delete source images
            for (String filename : imageFilenames) {
                File file = new File(filename);
                file.delete();
            }
            
        // Deal with prettier exception handling later
        } catch (CmdLineException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println();
            System.err.println("Example usage: java -jar MachineLabel.jar " + parser.printExample(OptionHandlerFilter.ALL) + " DATA1 DATA2 DATA3");
            System.err.println();
            parser.printUsage(System.err);
            //e.printStackTrace();
            System.exit(1);
        } catch (BarcodeException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (OutputException | IOException e) {
            e.printStackTrace();
            System.exit(1);            
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MachineLabel machinelabel = new MachineLabel();
        machinelabel.run(args);
    }
    
    private static String[] arrayJoin(String[] a, String[] b) {
        String[] output = new String[a.length + b.length];
        System.arraycopy(a,0,output,0,a.length);
        System.arraycopy(b,0,output,a.length,b.length);
        return output;
    }
    
    // Stack/append images using native java methods
    private static void stackImages(String outputFileName, String[] imageFileName, int spacing) throws IOException {
        //System.out.println(System.getProperty("user.dir"));

        // space between stacking
        int stackHeight = spacing;
        
        int srcWidth;
        int srcHeight;
        int totalHeight = 0;
        int maxWidth = 0;
        
        // load individual source images
        BufferedImage[] buffImage = new BufferedImage[imageFileName.length];
        for (int n = 0; n < imageFileName.length; n++) {
            
            buffImage[n] = ImageIO.read(new File(imageFileName[n])); // need to fix this .png hack
            
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
        System.err.println("Created: " + outputFileName);
    }
    
}
