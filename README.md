# IGCapT
The IGCAPT allows electric utility personnel to perform a static analysis of their communications network supporting smart grid architectures from a network utilization and performance standpoint. IGCAPT is meant to be a "first order" planning tool - the tool will accommodate estimates and best available data alongside default settings, which the user can then improve over time.

Other Software
--------------
Idaho National Laboratory is a cutting edge research facility which is a constantly producing high quality research and software. Feel free to take a look at our other software and scientific offerings at:

Primary Technology Offerings Page, <https://www.inl.gov/inl-initiatives/technology-deployment>

Supported Open Source Software, <https://github.com/idaholab>

Raw Experiment Open Source Software, <https://github.com/IdahoLabResearch>

Unsupported Open Source Software, <https://github.com/IdahoLabCuttingBoard>



1) BUILD / INSTALL
------------------

IGCAPT requires NetBeans IDE 8.2 to open and build the project.  NetBeans
IDE 8.2 can be downloaded at <https://netbeans.org/downloads/>.  NetBeans
IDE 8.2 requires Java SE Development Kit (JDK) version 8.  JDK 8 can be
downloaded at <http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>.

1) Open the IGCAPT project file by selecting "Open Project" from the "File" drop
down menu.
2) Modify the dir attribute of the <fileset\> element in the build.xml file to the location of the jaxb files in your NetBeans install.

	Linux example

		"~/netbeans-8.2/ide/modules/ext/jaxb"

	Windows example

		"C:/Program Files (x86)/NetBeans 8.2/ide/modules/ext/jaxb"

3) Build the IGCAPT project by right-clicking on the project and select
"Build".

**_BUILD OUTPUT DESCRIPTION_**

When you build an Java application project that has a main class, the IDE
automatically copies all of the JAR files on the projects classpath to your
projects dist/lib folder. The IDE also adds each of the JAR files to the
Class-Path element in the application JAR files manifest file (MANIFEST.MF).

Notes:

* If two JAR files on the project classpath have the same name, only the first
JAR file is copied to the lib folder.
* Only JAR files are copied to the lib folder.  If the classpath contains other
types of files or folders, these files (folders) are not copied.
* If a library on the projects classpath also has a Class-Path element
specified in the manifest,the content of the Class-Path element has to be on
the projects runtime path.
* To set a main class in a standard Java project, right-click the project node
in the Projects window and choose Properties. Then click Run and enter the
class name in the Main Class field. Alternatively, you can manually type the
class name in the manifest Main-Class element.

2) EXECUTE
--------------

IGCAPT requires Oracle Java SE Runtime Environment (JRE) 8 to execute.  JRE 8 can be
downloaded at
<http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html>.

To run IGCAPT from the command line, change directory to the location where IGCAPT was installed/built.  Then type the following:


**_Linux_**

Issue the following commands to install JRE 8

$ sudo -E apt-add-repository ppa:webupd8team/java

$ sudo apt-get update

$ sudo apt-get install oracle-java8-installer

To run IGCAPT

$ chmod +x runIGCAPTgui.sh

$ sh runIGCAPTgui.sh

**_Windows_**

Download and execute appropriate JRE 8 install .exe

To run IGCAPT

C:\> runIGCAPTgui

3) DIRECTORY DESCRIPTION
------------------------

_/build, /dist_

Directories containing files from the build process.

_/lib_

Libraries required by IGCAPT, Jung, OSM, etc.

_/nbproject_

NetBeans IDE 8.2 project and related files.

_/scenarios_

A collection of sample topology files to load.

_/sgicons_

Icon files required by IGCAPT to represent various network components.

_/src_

IGCAPT source code files.

License
------------------

  Copyright 2018 Battelle Energy Alliance, LLC
  
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/> or
  write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA.

Author contact information:

1. Kurt Derr (kur) <kurt.derr@inl.gov> Idaho National Laboratory P.O. Box 1625, MS 3770 Idaho Falls, ID 83415
2. Joe Frazier (FRAZJD) <joe.frazier@inl.gov> Idaho National Laboratory P.O. Box 1625, MS 3520 Idaho Falls, ID 83415
3. Jeffrey Young (ymj) <jeffrey.young@inl.gov> Idaho National Laboratory P.O. Box 1625, MS 3755 Idaho Falls, ID 83415
