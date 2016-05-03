#OsParkingCam- Korean, English, 3 real devices

OsParkingCam- supports both English & Korean GUI, simulator and real hardware idevice support fo 3 kinds of devices(camera, E-board and gate bari), needs OsParking-DB(another OsParking repository)

OsParking (pronounced 'Oz' Parking, as in The Wizard of Oz) is an Open Source Parking Lot Management Software. In an extended sense, it is a term which describes a simulation package that includes OsParking(manager) and three device simulators(Camera, E-Board, GateBar).

OsParking software assumes the use of LPR (License Plate Recognition) module to recognize car tag numbers.

Open source Parking, Inc.

April 30, 2016

-

Softwares Needed to Run OsParkingCam

1. JRE 1.8.0 or later

2. MySQL 5.6.24 or later

3. ANPRS LPR module (Proprietary S/W, need to be purchased seperately)

Additionally Needed Softwares for the Developers

1. IDE used: netbeans 8.0.2 or later

2. IDE augmenting Maven : apache-maven-3.3.3 or later

3. Java Compiler: JDK 1.8.0_45 or later

4. (Optionally) Toad for MySQL 7.5.0 or later

-

After the Maven is installed, the rs2xml.jar need to be registered manually as follows(this jar file is in 'lib' folder of this repository):

>C:\DOS> // first move to the directory where this jar file exists

>C:\DOS> mvn install:install-file -Dfile=rs2xml.jar -DgroupId=net.proteanit.sql -DartifactId=rs2xml -Dversion=1.0 -Dpackaging=jar
.
