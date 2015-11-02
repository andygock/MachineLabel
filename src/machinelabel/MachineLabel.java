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
            Runtime rt = Runtime.getRuntime();
            String outputfile = machineName + ".png";

            // Determine what platform we are running on
            String platform = System.getProperty("os.name");
            
            String[] cmdConvert = null;
            String[] cmdRemove = null;
            
            if (platform.startsWith("Windows")) {
                
                // Windows only - requires ImageMagick and installed and in PATH
                cmdConvert = new String[] { "cmd", "/c", "convert" };
                cmdRemove = new String[] { "cmd", "/c", "del" };
                
            } else if (platform.equals("Mac OS X")) {
                
                // Assumes install of ImageMagick in /usr/local/bin - e.g Homebrew
                cmdConvert = new String[] { "/usr/local/bin/convert" };
                cmdRemove = new String[] { "rm", "-f" };
                
            } else {
                
                // Unsupported platform
                // This should run fine on Linux, just haven't tested it...
                System.err.println("Platform not supported: " + platform);
                System.exit(1);
                
            }

            // Command line parameters to create blank PNG, used as a spacer between barcodes
            // Run with ImageMagick "convert"
            String[] cmdCreateBlankParams = new String[] {
                "-size", "1x15",
                "xc:#ffffff",
                "blank.png"
            };
            
            // Command line parameters to append different images together
            // Run with ImageMagick "convert"
            String[] cmdCreateLabelParams = null;
            
            if (args.length == 3) {
                // ip address supplied
                cmdCreateLabelParams = new String[] {
                    // convert
                    "blank.png",
                    "machine_name.png",
                    "blank.png",
                    "mac_address.png",
                    "blank.png",
                    "ip_address.png",
                    "blank.png",
                    "-gravity", "center",
                    "-append",
                    outputfile
                };
            } else {
                // no ip address
                cmdCreateLabelParams = new String[] {
                    // convert
                    "blank.png",
                    "machine_name.png",
                    "blank.png",
                    "mac_address.png",
                    "blank.png",
                    "-gravity", "center",
                    "-append",
                    outputfile
                };                
            }
            
            // Files to delete after conversion
            // Run with OS specific remove or delete command
            String[] cmdDeleteLabelParams = null;
            
            if (args.length == 3) {
                // ip address supplied
                cmdDeleteLabelParams = new String[] {
                    // rm
                    "blank.png",
                    "machine_name.png",
                    "mac_address.png",
                    "ip_address.png"
                };
            } else {
                // no ip address supplied
                cmdDeleteLabelParams = new String[] {
                    // rm
                    "blank.png",
                    "machine_name.png",
                    "mac_address.png"
                };                
            }

            // Run these 3 commands to create the barcode label
            rt.exec(arrayJoin(cmdConvert, cmdCreateBlankParams)).waitFor();
            rt.exec(arrayJoin(cmdConvert, cmdCreateLabelParams)).waitFor();
            rt.exec(arrayJoin(cmdRemove, cmdDeleteLabelParams)).waitFor();
            
        } catch (BarcodeException | OutputException | IOException | InterruptedException e) {
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
    
}
