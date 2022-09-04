# Annotated logging
Log statements at method level as annotations - don't pollute your business logic with log statements 


## Usage
This library allows defining application logs at a method level.
It supports both pre- and post-method logging with nice annotations.

Log messages support expression language which can reference either method input parameters
or return values.

Logging annotations are supported on methods with all visibility modifiers (including private).

### Basic log syntax

@LogBefore
```java 
@LogBefore("Log this message before method invocation")
public void doSomething() {
    ...
}
```

@LogAfter
```java 
@LogAfter("Log this message after method completion")
public void doSomething() {
    ...
}
```

By default, log statements are logged with `INFO` level.
This, however, can be changed or defined explicitly, as shown below.

A single method can be annotated with multiple logging annotations.

### Multiple annotations with different levels
This example shows an example of annotating a single method with multiple
logging annotations with different levels, logging input parameter and return value:


```java 
@LogBefore("Converting input value of type {value.getClass().getSimpleName()} to String...", level = INFO)
@LogAfter("Converted value: {r}", level = DEBUG)
public String convert(Object value) {
   ...
}
```

`r` is a reference to an object or a simple type being returned.
When it's an object, it's possible to reference its members, e.g.: `r.getName()`.

`r` reference is only available in `@LogAfter` annotations.

### Supported logging library
Currently, the library supports SLF4J logging facade library.

### Requirements
To make logging annotations work, a containing class needs to define 
`log` member field (name matters; case-sensitive) of `org.slf4j.Logger` type.
This means it also supports Lombok's  `@Slf4j` annotation.

### Expression language implementation
The library uses expression language feature provided by
`commons-jexl3` library from Apache Commons.