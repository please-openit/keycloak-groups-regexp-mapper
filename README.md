# Protocol mapper group filter membership with regexp

*By please-open.it*

This mapper allows to map groups that only fits to a regexp (by name). 

It avoids mapping all user groups into a token.

## Installation

```
mvn clean install
```

Copy generated JAR file from "deployment" directory into "providers" in Keycloak. Restart Keycloak.

## Usage

We recommend a dedicated scope for this mapper.

Add a mapper with "Add Mapper" then "by configuration"

![alt text](image.png)

The configuration is the same as the "groups" mapper in Keycloak, except on "regexp" field ; 

![alt text](image-1.png)


The module use "Pattern" : https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html