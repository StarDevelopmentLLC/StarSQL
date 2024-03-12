---
hide:
  - toc
---

# TypeHandlers
TypeHandlers are the recommended way to handle classes that you want to save to a single field. These are used by the library itself for handling the default supported types.  
Unlike with [ObjectCodecs](au-objectcodec.md), TypeHandlers are registered to the `DatabaseRegistry` or the `Database` and are shared within that context.  
If it is registered to the Registry, then all databases in that registry will use that type handler, if it is to the database, then just that database will use that TypeHandler.  

## Basic Requirements
TypeHandlers have basic requirements that are needed, and are provided via implementing classes, or just creating a new instance of the parent and passing in the values.  
### Main Class
The main Java Class to check against for the type, this is the type in the field of the table class.  
### Serializer
This takes in a Java Object and converts it into a SQL Object. Please see your database documentation for Java <-> SQL Object Conversions
### Deserializer
This is the opposite of a serializer and takes a SQL Object and converts it back to a Java Object. 
### Optional: Additional Classes
You can also specify additional classes that can be counted for the type handler. The main example of this is using primitives and their wrappers

## Creation
The recommended way is to create a child class that extends from `SQLTypeHandler` and implements the constructor. I am using anonymous classes here for this beginning one to show the full code.  
The first argument to the constructor is the main class for the TypeHandler

```java
public class PositionTypeHandler extends SQLTypeHandler {
    public PositionTypeHandler() {
        super(Position.class, "varchar(100)", new TypeSerializer<SQLDatabase>() {
            @Override
            public Object serialize(FieldModel<SQLDatabase> fieldModel, Object o) {
                Position pos = (Position) o;
                return pos.getX() + ":" + pos.getY() + ":" + pos.getZ();
            }
        }, new TypeDeserializer<SQLDatabase>() {
            @Override
            public Object deserialize(FieldModel<SQLDatabase> fieldModel, Object o) {
                if (o instanceof String str) {
                    String[] split = str.split(":");
                    if (split.length == 3) {
                        return new Position(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                    }
                }
                return null;
            }
        });
    }
}
```

If we replace this with a lambda, we get the shortened code
```java
public class PositionTypeHandler extends SQLTypeHandler {
    public PositionTypeHandler() {
        super(Position.class, "varchar(100)", (fieldModel, o) -> {
            Position pos = (Position) o;
            return pos.getX() + ":" + pos.getY() + ":" + pos.getZ();
        }, (fieldModel, o) -> {
            if (o instanceof String str) {
                String[] split = str.split(":");
                if (split.length == 3) {
                    return new Position(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                }
            }
            return null;
        });
    }
}
```

I do plan on adding a protected constructor that doesnt have the two serializers to make it easier to do these, but for now, this is how this is done.  

## Registration
Then you can choose one to do one of the following:  
Provide an instance to the `addTypeHandler` method in the DatabaseRegistry OR  
Provide an instance to the `addTyypeHandler` method in the Database.  
**This must be done before the class that uses that type is processed, this is done when it is registered to the database**
```java
registry.addTypeHandler(new PositionTypeHandler());
database.addTypeHandler(new PositionTypeHandler());
```

Once registered to one of the two, you do not need to do anything else. It will behave just like the default type handlers now.  