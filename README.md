# Website-Adoptium Project

## For New Contributors
### GitHub Organization using ZenHub
- [How to view the ZenHub board](https://github.com/AdoptOpenJDK/website-adoptium/wiki/ZENHUB:-Creating-and-Working-with-Issues#working-with-issues-using-zenhub)
- [Sprints using ZenHub pipelines](https://github.com/AdoptOpenJDK/website-adoptium/wiki/ZENHUB:-Creating-and-Working-with-Issues#sprints-using-zenhub-pipelines)
- [Creating issues](https://github.com/AdoptOpenJDK/website-adoptium/wiki/ZENHUB:-Creating-and-Working-with-Issues#creating-issues) (templates, labels and more...)
- [How to create PR-Previews](https://github.com/AdoptOpenJDK/website-adoptium/wiki/Creating-PR-Previews)

## Updating test api
```shell
curl https://staging-api.adoptopenjdk.net/v3/assets/latest/11/hotspot -o src/test/resources/api-staging/v3_assets_latest_11_hotspot.json
```
