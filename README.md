
# Enonic CMS Community Edition

Welcome to the home of Enonic CMS Community Edition. Here you will find all source code for the product.

## Checking out the code

To check out all of the code, you have to have Git installed. If you have not git installed, please see instructions [here](http://help.github.com/set-up-git-redirect). First clone
the repository with the following command:

	git clone git@github.com:enonic/cms-ce.git

## Building with Maven

Build all code and run all tests including integration tests:

    mvn clean install

Build all code skipping integration tests:

    mvn -DskipITs clean install

Build all code skipping all tests:

    mvn -DskipTests clean install

## Building with Gradle

We are now in the process of switching to Gradle for build. At this time we are building with Gradle version 1.0 Milestone 8, but if you do not have
gradle installed you can use the bundled gradle wrapper.

	./gradlew 
	
Build all code and run all tests including integration tests:

    gradle clean build

Build all code skipping integration tests:

    gradle clean build -x :cms-itest:test

Build all code skipping all tests:

    gradle clean build -x test

## License

This software is licensed under AGPL 3.0 license. See full license terms [here](http://www.enonic.com/license). Also the distribution includes
3rd party software components. The vast majority of these libraries are licensed under Apache 2.0. For a complete list please 
read [NOTICE.txt](https://github.com/enonic/cms-ce/raw/master/modules/cms-distro/src/resources/NOTICE.txt).

	Enonic CMS
	Copyright (C) 2000-2011 Enonic AS.

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as
	published by the Free Software Foundation, either version 3 of the
	License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.

	You should have received a copy of the GNU Affero General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
