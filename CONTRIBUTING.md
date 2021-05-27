# Contributing

## Working with Issues using **ZenHub**
ZenHub is a management tool used to organize user-stories and issues into 2-week sprints.

There are two options to view the ZenHub board:
1. Log into https://app.zenhub.com/login using your GitHub account. 
2. Download the ZenHub for GitHub extension on https://www.zenhub.com/extension. This allows you to view the ZenHub board in the corresponding GitHub repository.
<br/><br/>  

### Sprints using ZenHub Pipelines
The Website-Adoptium project is organized into repeating 2 week sprints. 
Issues created on ZenHub are organized into different pipelines:

**New Issues** - Newly created issues, if not yet put into a pipeline, appear here.

**Product Backlog** - Issues which are not being worked on in the current sprint.

**Sprint Backlog** - Issues in the current sprint that have not been started yet. If these Issues aren't completed in the current sprint, they are moved to the next sprint.

**In Progress** - Issues in the current sprint that are currently under development. If these Issues aren't completed in the current sprint, they are moved to the next sprint.

**Review/QA** - Completed issues that still require a review from other developers. If these Issues are still in the Review/QA pipeline at the end of the sprint they are moved to the next sprint.

**Closed** - Completed issues that don't require a review
<br/><br/>  
  
### Creating Issues
#### Issue Templates
When creating a **user story**, **feature request** or **bug report** use the corresponding templates. The templates provide a rough outline of how to structure the issue.

The templates can be found [here](https://github.com/AdoptOpenJDK/website-adoptium/tree/main/.github/ISSUE_TEMPLATE).

#### Issue Labels
**user story** - A user story is an informal, general explanation of a software feature written from the perspective of the end user. They usually describe a single feature that is to be implemented. User stories are also labelled as Epics as they usually consist of multiple issues.

**Epic** - Epics are issues that encompass multiple other issues.

**Documentation** - Issues that request updates to our documentation

**Feature** - Issues that enhance the code or documentation of the repo in any way

**Bug** - Issues that are problems in the code as reported by the community

**Infrastructure** - Issues that impact the infrastructure 

**Important** - High priority issues that are not buildbreakers but may still require more attention than others

**Help wanted** - Issues that need an extra hand helping out with them

**Question** - Issues that are queries about the code base or potential problems that have been spotted

**Testing** - Issues that enhance or fix our test suites

**WeMakeIt** - These issues can be ignored by contributors that don't belong to the 'ZHAW PM4 Programming Team'.

The complete list of labels can be found [here](https://github.com/AdoptOpenJDK/website-adoptium/labels)

#### Sprints
When moving an issue into the pipeline 'Sprint Backlog' or when creating the issue the sprint to which the issue belongs should be specified.
#### Estimates
An estimate of how much effort it would take to resolve the issue. The points aren't directly linked to a specific amount of time, since different developers work at different speeds.
#### Epic
Each issue that isn't an Epic itself, should be assigned to an Epic if possible.



## PR-Previews

### How is a PR-Preview created?
Every Pull Request in to the Main Branch will automatically create a viewable deployment environment on Heroku.


### Where to find the Deployment link?
After a pull request has been created, you will find a button "View Deployment" at the end of the commit chain, which will redirect you to the deployment environment from Heroku. 

![Where to find Deployment link alt text](https://github.com/AdoptOpenJDK/website-adoptium/blob/main/docs/images/whereToFindDeploymentLink1.png)

or
  
![Where to find Deployment link alt text2](https://github.com/AdoptOpenJDK/website-adoptium/blob/main/docs/images/whereToFindDeploymentLink2.png)

### How long are PR-Previews available?
A PR-Preview has a life span of 30 days after the last commit on the branch that will be merged in to the Main branch.


## How to add new Documentation to the Website

1. Write the doc in [AsciiDoc](https://asciidoctor.org/docs/asciidoc-writers-guide/)
2. Add ```ifndef::imagesdir[:imagesdir: ../images]``` to the start of the AsciiDoc
3. Create a new folder for your doc: ```website-adoptium/src/documentation/[docName]```

![Image 3.](https://github.com/AdoptOpenJDK/website-adoptium/blob/main/docs/images/create_new_folder_for_doc.PNG)

4. Add the AsciiDoc into this folder with the name index.adoc (for english) or index_[lang].adoc (ex. German: index_de.adoc)

![Image 4.](https://github.com/AdoptOpenJDK/website-adoptium/blob/main/docs/images/insert_adoc_into_folder.PNG)

5. Add images and other resources into the ```website-adoptium/src/documentation/images``` folder
6. Push the changes onto GitHub

The doc should be viewable on the website under ```/documentation/[docName]```.
