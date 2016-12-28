# sf2
KauaiLabs Robotics Sensor Fusion Framework

![SF2 Overview](http://sf2.kauailabs.com/wp-content/uploads/2016/12/SF2_Overview.png)

The Kauai Labs [Sensor Fusion Framework (SF2)](http://sf2.kauailabs.com), a key component of the Build Better Robots® platform, is comprised of software libraries and tools enabling robot software developers to quickly and easily fuse data from various sensors, enabling several key new features for autonomous and driver-assisted navigation:

- [Video Processing Latency Correction](http://sf2.kauailabs.com/examples/video-processing-latency-correction/)
- IMU Odometry
- Robot Localization

<i>NOTE:  The initial SF2 release includes support for Video Processing Latency Correction.  Future releases are planned to add fusion algorithms including IMU Odometry and Robot Localization.</i>

Designed to integrate easily into FRC and FTC Robot Control Systems, SF2:

- acquires data streams from multiple sensors
- synchronizes sensor data streams
- interpolates data from low-sample rate sensors
- fuses sensor data streams using state-of-the-art algorithms
- includes tools for debugging, data visualization and offline data analysis

SF2 works seamlessly with Kauai Labs Sensors (navX-MXP, navX-Micro) and supports multiple robot platforms including FIRST FRC robotics, FIRST FTC robotics and Linux/Windows-based robot control systems.  SF2 is an open framework and will work with third-party sensors.

SF2 includes tutorials and examples with source code in several popular programming languages to streamline the integration of advanced sensor fusion into a robot control system.
