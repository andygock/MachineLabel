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

    // ref: http://barbecue.sourceforge.net/apidocs/net/sourceforge/barbecue/BarcodeFactory.html
    private enum Codetype {
        CODE_2OF7,
        CODE_3OF9,
        CODE_BOOKLAND,
        CODE_CODABAR,
        CODE_CODE128,
        CODE_CODE128A,
        CODE_CODE128B,
        CODE_CODE128C,
        CODE_CODE39,
        CODE_EAN128,
        CODE_EAN13,
        CODE_GTIN,
        CODE_INT2OF5,
        CODE_MONARCH,
        CODE_NW7,
        CODE_PDF417,
        CODE_POSTNET,
        CODE_RANDOMWEIGHT_UPCA,
        CODE_SCC14_SHIPPING_CODE,
        CODE_SIN,
        CODE_SSCC18,
        CODE_STD2OF5,
        CODE_UPCA,
        CODE_USD3,
        CODE_USD4,
        CODE_USPS,
    }    
    
    private String data;
    
    // default bar heights and widths
    private int barHeight = 70;
    private int barWidth = 3;

    private Codetype codetype = Barcoder.Codetype.CODE_CODE128;
    
    public Barcoder(String data) {
        this.data = data;
    }
    
    public void setCodeType(String type) {
        switch (type) {
            case "2of7":
                codetype = Codetype.CODE_2OF7;
                break;
            case "3of9":
                codetype = Codetype.CODE_3OF9;
                break;
            case "bookland":
                codetype = Codetype.CODE_BOOKLAND;
                break;
            case "codabar":
                codetype = Codetype.CODE_CODABAR;
                break;
            case "code128":
                codetype = Codetype.CODE_CODE128;
                break;
            case "code128a":
                codetype = Codetype.CODE_CODE128A;
                break;
            case "code128b":
                codetype = Codetype.CODE_CODE128B;
                break;
            case "code128c":
                codetype = Codetype.CODE_CODE128C;
                break;
            case "code39":
                codetype = Codetype.CODE_CODE39;
                break;
            case "ean128":
                codetype = Codetype.CODE_EAN128;
                break;
            case "ean13":
                codetype = Codetype.CODE_EAN13;
                break;
            case "gtin":
                codetype = Codetype.CODE_GTIN;
                break;
            case "int2of5":
                codetype = Codetype.CODE_INT2OF5;
                break;
            case "monarch":
                codetype = Codetype.CODE_MONARCH;
                break;
            case "nw7":
                codetype = Codetype.CODE_NW7;
                break;
            case "pdf417":
                codetype = Codetype.CODE_PDF417;
                break;
            case "postnet":
                codetype = Codetype.CODE_POSTNET;
                break;
            case "rupca":
                codetype = Codetype.CODE_RANDOMWEIGHT_UPCA;
                break;
            case "scc14":
                codetype = Codetype.CODE_SCC14_SHIPPING_CODE;
                break;
            case "sin":
                codetype = Codetype.CODE_SIN;
                break;
            case "sscc18":
                codetype = Codetype.CODE_SSCC18;
                break;
            case "std2of5":
                codetype = Codetype.CODE_STD2OF5;
                break;
            case "upca":
                codetype = Codetype.CODE_UPCA;
                break;
            case "usd3":
                codetype = Codetype.CODE_USD3;
                break;
            case "usd4":
                codetype = Codetype.CODE_USD4;
                break;
            case "usps":
                codetype = Codetype.CODE_USPS;
                break;
            
            default:
                break;
        }
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

        switch (codetype) {
            case CODE_2OF7:
                barcode = BarcodeFactory.create2of7(text);
                break;
            case CODE_3OF9:
                barcode = BarcodeFactory.create3of9(text, false);
                break;
            case CODE_BOOKLAND:
                barcode = BarcodeFactory.createBookland(text);
                break;
            case CODE_CODABAR:
                barcode = BarcodeFactory.createCodabar(text);
                break;
            case CODE_CODE128:
                barcode = BarcodeFactory.createCode128(text);
                break;
            case CODE_CODE128A:
                barcode = BarcodeFactory.createCode128A(text);
                break;
            case CODE_CODE128B:
                barcode = BarcodeFactory.createCode128B(text);
                break;
            case CODE_CODE128C:
                barcode = BarcodeFactory.createCode128C(text);
                break;
            case CODE_CODE39:
                barcode = BarcodeFactory.createCode39(text, false);
                break;
            case CODE_EAN128:
                barcode = BarcodeFactory.createEAN128(text);
                break;
            case CODE_EAN13:
                barcode = BarcodeFactory.createEAN13(text);
                break;
            case CODE_GTIN:
                barcode = BarcodeFactory.createGlobalTradeItemNumber(text);
                break;
            case CODE_INT2OF5:
                barcode = BarcodeFactory.createInt2of5(text);
                break;
            case CODE_MONARCH:
                barcode = BarcodeFactory.createMonarch(text);
                break;
            case CODE_NW7:
                barcode = BarcodeFactory.createNW7(text);
                break;
            case CODE_PDF417:
                barcode = BarcodeFactory.createPDF417(text);
                break;
            case CODE_POSTNET:
                barcode = BarcodeFactory.createPostNet(text);
                break;
            case CODE_RANDOMWEIGHT_UPCA:
                barcode = BarcodeFactory.createRandomWeightUPCA(text);
                break;
            case CODE_SCC14_SHIPPING_CODE:
                barcode = BarcodeFactory.createSCC14ShippingCode(text);
                break;
            case CODE_SIN:
                barcode = BarcodeFactory.createShipmentIdentificationNumber(text);
                break;
            case CODE_SSCC18:
                barcode = BarcodeFactory.createSSCC18(text);
                break;
            case CODE_STD2OF5:
                barcode = BarcodeFactory.createStd2of5(text);
                break;
            case CODE_UPCA:
                barcode = BarcodeFactory.createUPCA(text);
                break;
            case CODE_USD3:
                barcode = BarcodeFactory.createUSD3(text, false);
                break;
            case CODE_USD4:
                barcode = BarcodeFactory.createUSD4(text);
                break;
            case CODE_USPS:
                barcode = BarcodeFactory.createUSPS(text);
                break;
            default:
                barcode = BarcodeFactory.createCode128(text);
                break;
        }
        
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
