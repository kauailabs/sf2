REM Generate Java Public Class Library Documentation

pushd ..

REM FRC Library

pushd core\java\src
javadoc -d ./docs -overview ./overview.htm -subpackages com.kauailabs.sf2
popd

pushd frc\java\src
javadoc -d ./docs -overview ./overview.htm -subpackages com.kauailabs.sf2
popd

Generate C++ Public Class Library Documentation

pushd core\c++\doxygen
doxygen doxygen.cfg
popd

pushd frc\c++\doxygen
doxygen doxygen.cfg
popd

popd
