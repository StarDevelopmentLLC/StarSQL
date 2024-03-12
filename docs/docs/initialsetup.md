---
hide:
  - navigation
---

# Initial Setup
This page will cover some initial things that you need in order to use this library and get things working. 

## Logger Reference
Loggers aren't required, but encouraged, StarSQL has a `getLogger` method that creates a default logger instance. I am just calling it here and storing it in a constant.  
```java
public static final Logger LOGGER = StarSQL.getLogger();
```

## Database Registry
This class stores references to our database and does some things for us that are a bit useful and has some default type parsers that are shared across the database registered to it.  
For this one, I am creating a field for it, and passing in th elogger reference above. 
```java
public static final SQLDatabaseRegistry registry = new SQLDatabaseRegistry(LOGGER);
```
Note: If you are using StarCore, a Registry is provided to the Bukkit Services Manager

You must call the `setup` method at some point. I prefer to do this after the other stuff, but it will still work even if you call it first. I will cover this later.  

## Properties
This class has sub-classes for each of the types and they define what you can do for each one.  
Not all settings are required, and if any required fields are missing or invalid, exceptions are thrown

```java
MySQLProperties properties = new MySQLProperties().setDatabaseName("test").setHost("localhost").setUsername("root").setPort(3306).setPassword("");
```

Please note: I am using a development installation of MySQL with XAMPP and PhpMyAdmin installed.  
The Database itself must exist in the actual database. This is a limitation of this library and allows database admins a little more control. 

## Database 
Next is the actual database instance. We will be needing this for quite a few of the next steps though
```java
SQLDatabase database = new MySQLDatabase(StarSQL.getLogger(), properties);
```
Then you want to register this to the registry. If you have already called the `DatabaseRegistry#setup` method, you want to wait, if you haven't, you can do it now.  
```java
registry.register(database);
```

Now I am going to call the setup method on the registry
```java
registry.setup();
```

You can either continue with a [basic example](basicusage.md) or an advanced example under the "Advanced Usage" tab.  

## Full Code (So Far) (Ignoring Imports)
```java 
public class App {
    
    public static final Logger LOGGER = StarSQL.getLogger();
    public static final SQLDatabaseRegistry registry = new SQLDatabaseRegistry(LOGGER);
    
    public static void main(String[] args) throws Exception {
        MySQLProperties properties = new MySQLProperties().setDatabaseName("test").setHost("localhost").setUsername("root").setPort(3306).setPassword("");
        SQLDatabase database = new MySQLDatabase(LOGGER, properties);
        registry.register(database);
        registry.setup();
    }
}
```

I do recommending handling the exception on the setup method separately, but this is a tutorial and I am not going to do that.  
Running this does nothing though as there is no interaction with the database whatsoever.