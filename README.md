## Introduction

A tool used to generate a basic label composed of one or more barcodes stacked vertically.

## Requirements

* [Barbecue - Java barcode generator](http://sourceforge.net/projects/barbecue/files/barbecue/) library (BSD License).
* [Apache Commons Codec](https://commons.apache.org/proper/commons-codec/) library. (Apache License v2)
* [args4j](http://args4j.kohsuke.org/) Java command line arguments parser (MIT License)

Download the latest JAR files and place it in the `lib/` directory. This Git repository will have the latest version (at the time of writing) already committed

## Information

Directory structure is as a NetBeans project.

Compile using NetBeans. To run the application, an example use would be:

    java -jar MachineLabel.jar "MACHINE-NAME" "01:02:03:04:05:06" "192.168.0.2"

This should generate a PNG image into the working directory with the filename `output.png`.

For any field which is in a MAC address format with a `-` separators, the application will internally substitute them to `:`.

## Command line usage

    Example usage:
     java -jar MachineLabel.jar  -h HEIGHT -o FILENAME -s SPACE -w WIDTH DATA1 DATA2 DATA3

    Options and arguments:
     DATAx       : Data fields, minimum of 1 field required
     -h HEIGHT   : Bar height, optional (default: 34)
     -o FILENAME : Output file name, optional (default: output.png)
     -s SPACE    : Spacing, optional (default: 15)
     -w WIDTH    : Bar width, optional (default: 3)

## Testing

To test, build the JAR file in Netbeans by using Run, Build Project. Then perform:

    java -jar dist/MachineLabel.jar MACHINE-1 "01:02:03:04:05:06" "192.168.0.2"
    java -jar dist/MachineLabel.jar MACHINE-2 "07:08:09:0A:0B:0C"

Example output (with IP address):

![Label with IP address](http://s.agock.com/MachineLabel/MACHINE-1.png)

Example output (without IP address):

![Label without IP address](http://s.agock.com/MachineLabel/MACHINE-2.png)
