# Website-Adoptium Project

### GitHub Organization using ZenHub
- [How to view the ZenHub board](https://github.com/AdoptOpenJDK/website-adoptium/wiki/ZENHUB:-Creating-and-Working-with-Issues#working-with-issues-using-zenhub)
- [Sprints using ZenHub pipelines](https://github.com/AdoptOpenJDK/website-adoptium/wiki/ZENHUB:-Creating-and-Working-with-Issues#sprints-using-zenhub-pipelines)
- [Creating issues](https://github.com/AdoptOpenJDK/website-adoptium/wiki/ZENHUB:-Creating-and-Working-with-Issues#creating-issues) (templates, labels and more...)
- [How to create PR-Previews](https://github.com/AdoptOpenJDK/website-adoptium/blob/main/CONTRIBUTING.md)

### Adding Docs to the Website
- [How to add new documentation to the website](https://github.com/AdoptOpenJDK/website-adoptium/blob/main/CONTRIBUTING.md#how-to-add-new-documentation-to-the-website)

## Running the server
Also available here: https://quarkus.io/guides/getting-started#packaging-and-run-the-application
```shell
git clone https://github.com/AdoptOpenJDK/website-adoptium/
cd website-adoptium/

# development
./mvnw compile quarkus:dev

# production
./mvnw package
# If you want to deploy your application somewhere (typically in a container), you need to deploy the whole quarkus-app directory.
java -jar target/quarkus-app/quarkus-run.jar
```

## Updating test api
```shell
curl https://staging-api.adoptopenjdk.net/v3/assets/latest/11/hotspot -o src/test/resources/api-staging/v3_assets_latest_11_hotspot.json
```
