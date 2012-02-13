# Gradle Build @ Enonic

This project holds the shared Gradle scripts and resources for Enonic projects. 

## Creating a new project

After creating a new Gradle based project you have to add a submodule for the shared build scripts. 

	git submodule add git@github.com:enonic/gradle-build.git gradle
	
This will add the submodule to the new project. After this, you must commit the added submodule
changes.

	git commit -m "Added shared gradle scripts" -a
	
And after this you should probably push the changes up to remote repository.

## Updating build sources

To be written...

	

	