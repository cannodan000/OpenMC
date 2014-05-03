#Contributing Guidelines
While we allow anyone to contribute to OpenMC. We have to set some guidelines to make the process as smooth as possible. If
you plan to get involved, please read everything and make sure you follow this guideline exactly.

##Contributing to the Server
There are three types of contributions you can make to the OpenMC server. A configuration change, a plugin addition/updating/removal, and
a server software update.

However, **you can only contribute one type in one pull request**. For example, you make a configuration change and add a new plugin. Your pull
request will be ignored. The only exception to this is when you are pre-configuring a new plugin (An example of this may include, but not limited to, changing default configuration files in a plugin or adding plugin permissions to certain groups).

If you plan to make a contribution to the server, please follow these guidelines.

###Configuration change
If you simply wish to change a configuration file (ex: you want to add a new rank or rank/derank someone), then
follow the basic rules below:

* Make sure the configuration change follows the plugin's instructions.

* Make sure your change has been fully tested and is non breaking

* NO CRLF line endings, LF only. Set your Gits 'core.autocrlf' to 'true'

* Some configuration files require tabs, while others require 4 spaces. Check which one your configuration
file uses and remain consistent.


###Plugin Addition/Updating
If you plan to add a new plugin, and/or update an existing plugin, please follow the rules below:

* .jar files only.

* Only plugins found ***AND*** approved on [Bukkit Dev](http://dev.bukkit.org/) are allowed. The only exception to this
are possible plugins made by GamezGalaxy.

* Be sure the plugin you're adding/updating is compatible with the current server version and software.

* If you are updating a plugin, **do not rename the plugin file**. Keep the filename the same as it was before.

* If you are adding a new plugin, please remove any versioning in the filename (ex: If you the file you downloaded is
named 'VoxelSniper-5.169.0-SNAPSHOT.jar', rename it to 'VoxelSniper.jar')

###Plugin removal
If you plan to remove a plugin, please be sure your pull request ***only*** removes the plugin, any files left behind by
the plugin and the plugin's data folder.

###Updating the server software
These kind of contributions are not taken lightly, and are most of the time made by the admins of the server. If you do
plan to update the server software, please be sure the version you update to has been ***fully tested***.

###Rules for any kind of contribution

* Do not commit the server log file.

* Do not commit any worlds

* Do not commit Window's Thumbs.db file

##Contributing to the OpenMC bot
_Coming soon_

##Pull request expectations
When submitting a pull request to OpenMC, you must format your pull request in a accordance to what kind of change you made.

###Title
> [PR Type] Brief Description
> PR Type can be B for 'OpenMC bot', CC for 'Configuration Change', PA for 'Plugin Addition', PU for 'Plugin Updating', PR for 'Plugin Removal', and SU for 'Server Update'
> 
> If you are adding/updating a plugin, or updating the server software. You must provide the version in the title.
>
> Example:
> [PA] Added WorldEdit v2.0

###Description
> A summary of what this pull request does should go here.
>
> ####Plugin/Server download location used
> If you are submitting a plugin addition/update, or updating the server software, ***you must provide the download location of the same file you included in the PR***.
>
> ####Reason for pull request
> Paragraphs on how this pull request will benefit the community and/or the server.
>
> ####Proof of testing
> Paragraphs describing how you tested this pull request and the results.
>
> If you are submitting a plugin addition/update or updating the server software, ***you must provide a MD5 checksum of all .jar files and executable/binary files***.
>
> ####Related PR(s):
> This section should link related pull requests or alternate pull requests if any exist. Each linked PR should have a reason on why it's linked next to it (ex: "PR #113 - Adds the same plugin but does not add permissions)
