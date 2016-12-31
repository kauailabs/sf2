The sf2-libs.zip file contains all sf2 libraries, and is intended for use 
by non-windows SF2 developers.

*NOTE to Windows Developers:  All of the libs in this .zip file are automatically installed 
as part of the setup.exe installer included within the SF2 latest build.*

Installation instructions for Linux:

# Create a "navx-mxp" directory underneath your home directory

cd ~
mkdir sf2
cd sf2

# Unzip the contents of navx-mxp-libs.zip to the new navx-mxp directory.

At this point, the instructions for the C++, Java and Libaries documented at the online SF2 site 
for the RoboRIO libraries (http://sf2.kauailabs.com/software/roborio-libraries/) may be
followed.  The only difference will be to replace the windows path to these libraries with the
Linux path the libraries.  Specifically, replace:

C:\Users\<username>\sf2

  with

~\sf2