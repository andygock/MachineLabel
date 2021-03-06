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

import java.io.File;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;


/**
 *
 * @author andy
 */
public class Barcoder {
    
    private String data;
    
    // default bar heights and widths
    private int barHeight = 70;
    private int barWidth = 3;
    
    public Barcoder(String data) {
        this.data = data;
    }
    
    public void setBarHeight(int barHeight) {
        this.barHeight = barHeight;
    }
    
    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }
    
    public String writePNG() throws BarcodeException, OutputException {
        String hash = Hash.sha1(data);
        makeBarcode(data, hash + ".png");
        return hash + ".png";
    }

    public String writePNG(String filename) throws BarcodeException, OutputException {
        makeBarcode(data, filename);
        return filename;
    }
    
    private void makeBarcode(String text, String filename) throws BarcodeException, OutputException {
        Barcode barcode;
        File imgFile;
        
        barcode = BarcodeFactory.createCode128B(text);
        barcode.setBarHeight(barHeight);
        barcode.setBarWidth(barWidth);
        
        imgFile = new File(filename);

        // this is useless ... but it helps get rid of the black line.
        // ref: http://sourceforge.net/p/barbecue/discussion/266281/thread/bb3d5344/
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BarcodeImageHandler.writePNG(barcode, baos);
        
        BarcodeImageHandler.savePNG(barcode, imgFile);         
    }
    
    
}
