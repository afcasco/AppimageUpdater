# AppimageUpdater
Automatically update an AppImage to the latest github release

Downloads and moves the latest AppImage from github releases if
it's not already detected in the specified folder.

<hr>

Repository configuration needs one of the following:

- Edit src/main/resources/application.properties file if running from your IDE.

- application.properties file in the same folder as the jar if run standalone.


File content should be:

- appimage.folder=/path/to/desired/folder
- repo.owner=repositoryOwnerName
- repo.name=repositoryName

