mset ZIP_UTILITY="C:\Program Files\7-Zip\7z"
REM Batch file to create the SF2 distributable packages

pushd ..
md dist
pushd dist

cp ../setup/Output_SF2/* .
cp ../build/target/* ./
cp ../LICENSE.txt ./

REM Delete any files copied to the dist directory which are not appropriate for distribution
REM del /S /Q .\roborio\c++\navXMXP_CPlusPlus_RobotExample\Debug\*.*
REM del /S /Q .\roborio\java\navXMXPSimpleRobotExample\build\*.*
REM rm  -r -f .\.svn

REM Create the zip file

rm ../sf2.zip
%ZIP_UTILITY% a ../sf2.zip *

REM Cleanup

popd
rm -r -f dist
popd

pushd ..
md dist2
pushd dist2

cp ../build/target/* ./
cp ../LICENSE.txt ./

REM Copy binary libraries to the "libraries" directory (for installation by those who don't run the setup program)
mkdir java\lib
mkdir java\src
mkdir java\javadoc
mkdir java\javadoc\core
mkdir java\javadoc\frc
REM mkdir java\examples
mkdir labview

REM Copy Java components
cp ../core/java/jar/* ./java/lib
cp ../frc/java/jar/* ./java/lib
cp -r ../core/java/src/* ./java/src
cp -r ../frc/java/src/* ./java/src
cp -r ../core/java/src/docs/* ./java/javadoc/core
cp -r ../core/java/src/docs/* ./java/javadoc/frc
REM rm -r ../roborio/java/navXMXP_*/bin
REM rm -r ../roborio/java/navXMXP_*/build
cp -r ../core/LabVIEW/* ./labview
cp -r ../frc/LabVIEW/* ./labview

cp ../build/version.txt .

cp -r ../build/sf2-libs-target/* .

REM Create the "libs" zip file

rm ../sf2-libs.zip
%ZIP_UTILITY% a ../sf2-libs.zip *

popd
rm -r -f dist2
popd
