# Contributing


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
