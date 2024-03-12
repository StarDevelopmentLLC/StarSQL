---
hide:
  - navigation
---
# Misc Configuration
This is just some simple things about configuring things about the structure and database that are short.  

## AutoIncrement Annotation
This annotation allows you to tell the library that the field is an auto-increment field

## DBNotNull Annotation
This is just a flag annotation to tell it that the column cannot have null values  
Named this way to prevent conflicts with other NotNull annotations

## ID Annotation
This is a legacy annotation, it just tells the library that the field is the "id", which is an int or long that is also the primary key  
You can get this by just making a field call "id" that is either an int or a long and it does the same thing.  

## Ignored Annotation
Tells the library to ignore the field, you can also have it auto-ignore fields that are `transient` if you don't want to depend on the library directly

## Name annotation
Allows you to override the name of a table or field

## Order annotation
Allows you specify an order to the columns of a table, useful for those that love to nit-pick at this (like me)

## Primary Key annotation
Used for non-int/long primary keys

## Unique annotation
Allows you to specify if the column can only have one of each value