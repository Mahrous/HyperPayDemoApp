MCS Mobile_Commerce_SDK_Developer_Sample_App_Android repository

Folder Name	Description


Contribution

Use git flow type process:
for any new tool or improvement of existing tool create branch "feature/your_branch_name_here";
once development is completed create pull request to develop branch in Bitbucket for others to review.
Update this README file with short description of your tool.

New Tools

Add as a reviewers:
original authors of other tools in this repository;
original authors of the tools created with the same technology stack (e.g. user scripts).
For headers follow format of existing tools with the same technology stack.
Always create README file. Use other tools README files as a template.
If your tool can be part of several repositories (e.g. it gathers information from both Bitbucket in Jira):
add it into repository #1;
create a folder and README file with the link to real tool location in repository #2;
add short decsription of the tool to the root README of both repositories.
Existing Tools Improvement

If there are two versions (e.g. we have chrome version for user scripts) don't forget to mirror your changes in both.

Add all contributors to this tool as a reviewers for your pull requests.

Add your name to the list of script authors after last name in the list.

You have an empty repository

To get started you will need to run these commands in your terminal.

New to Git? Learn the basic Git commands

Configure Git for the first time

	git config --global user.name "First Last"

	git config --global user.email "first.last@aciworldwide.com"

Working with your repository

I just want to clone this repository

If you want to simply clone this empty repository then run this command in your terminal.

	git clone ssh://git@bitbucket02.am.tsacorp.com:7999/mcs/mobile_commerce_sdk_developer_sample_app_android.git

My code is ready to be pushed

If you already have code ready to be pushed to this repository then run this in your terminal.

	cd existing-project

	git init

	git add --all

	git commit -m "Initial Commit"

	git remote add origin ssh://git@bitbucket02.am.tsacorp.com:7999/mcs/mobile_commerce_sdk_developer_sample_app_android.git
	
	git push -u origin master

My code is already tracked by Git

If your code is already tracked by Git then set this repository as your "origin" to push to.

	cd existing-project

	git remote set-url origin ssh://git@bitbucket02.am.tsacorp.com:7999/mcs/mobile_commerce_sdk_developer_sample_app_android.git

	git push -u origin master
	
	