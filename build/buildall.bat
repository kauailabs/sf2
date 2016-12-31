SET VER_MAJOR=1
SET VER_MINOR=0

SET ECLIPSEC_MARS=C:\Users\Scott\eclipse\cpp-mars\eclipse\eclipsec.exe
REM Build all binaries

REM CD to Root Directory
pushd ..

REM
REM Begin a command-line "clean build" of the SF2 core java library
REM
pushd .\core\java
call ant clean CreateJar
popd

REM
REM Begin a command-line "clean build" of the SF2 frc java library
REM

pushd .\frc\java
call ant clean CreateJar
popd

REM
REM Begin a command-line "clean build" of the navx frc C++ library
REM

REM pushd .\roborio\c++
REM rm -r -f ./build_workspace_luna
REM mkdir build_workspace_luna

REM %ECLIPSEC_MARS% -nosplash -application org.eclipse.cdt.managedbuilder.core.headlessbuild -data ./build_workspace_luna -import ./navx_frc_cpp -cleanBuild navx_frc_cpp/Debug
REM popd

REM
REM Begin a command-line "clean build" of the Debug version of the navx-mxp firmware
REM

REM Use the GIT checkin count as the SF2 revision number 

@echo off
for /f %%i in ('git rev-list --count --first-parent HEAD') do set VER_REVISION=%%i
set REVISION_STRING=%VER_MAJOR%.%VER_MINOR%.%VER_REVISION%
REM Place version string into setup script 
@echo on
echo %REVISION_STRING% > .\build\version.txt

REM Build FTC Library
REM pushd .\android\navx_ftc
REM rmdir /S /Q build
REM call gradlew.bat assembleDebug
REM call gradlew.bat assembleRelease
REM popd

REM Build setup program

copy .\setup\sf2-setup.iss .\setup\sf2-setup-orig.iss 
Powershell -command "(get-content .\setup\sf2-setup.iss) -replace ('0.0.000','%REVISION_STRING%') | out-file .\setup\sf2-setup.iss -encoding ASCII"
pushd build
call buildsetup_sf2.bat
popd
copy .\setup\sf2-setup-orig.iss .\setup\sf2-setup.iss
del .\setup\sf2-setup-orig.iss

popd
