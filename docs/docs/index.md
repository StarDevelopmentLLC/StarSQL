# StarSQL Wiki
Welcome to the StarSQL Wiki.   
This is provided as a tutorial to help developers use the StarSQL library as well as to document how things work within the library.  

## Choosing SQL Type
This library suppoirts H2, MySQL, Postgres and SQLite databases. For the purposes of the wiki/tutorial, MySQL is used.  
Not all SQL databases are made equal, please see your database documentation for the differences, however this library was made strictly for MySQL, then adpated to suite the others.  

## Dependency Information
### JitPack
In order to depend on StarSQL, you must have JitPack as a repo. 

**Maven**
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

**Gradle**
```groovy
maven { url 'https://jitpack.io' }
```

### Module
Then you replace {MODULE} with: `h2`, `mysql`, `postgres` or `sqlite`
And {VERSION} with whichever version you are using. 
**Maven** 
```xml
<dependency>
    <groupId>com.github.StarDevelopmentLLC.StarSQL</groupId>
	<artifactId>{MODULE}</artifactId>
	<version>{VERSION}</version>
</dependency>
```

**Gradle**
```groovy
implementation 'com.github.StarDevelopmentLLC.StarSQL:{MODULE}:{VERSION}'
```

## Next Steps
After choosing your type and depending on StarSQL, you want to move on to [Initial Setup](initialsetup.md) to see what to do next. 