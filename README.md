## Requirements

* [Barbecue - Java barcode generator](http://sourceforge.net/projects/barbecue/files/barbecue/) library (BSD license). Download the latest JAR file and place it in the `lib/` directory. This Git repository will have the latest version (at the time of writing) already committed.

## Information

Directory structure is as a NetBeans project.

Compile using NetBeans. To run the application, and example use would be:

    java -jar MachineLabel.jar "MACHINE-NAME" "01:02:03:04:05:06" "192.168.0.2"

The parameters are:

    1. Machine name
    2. MAC address
    3. IP address (optional)

This should generate a PNG image into the working directory in the format `MACHINE-NAME.png`.

The MAC address can be entered with either `-` or `:` separators. However, the application will internally substitute them to `:` as required.

## Testing

To test, build the JAR file with Run, Build Project. The run

    java -jar dist/MachineLabel.jar MACHINE-1 "01:02:03:04:05:06" "192.168.0.2"
    java -jar dist/MachineLabel.jar MACHINE-2 "07:08:09:0A:0B:0C"
