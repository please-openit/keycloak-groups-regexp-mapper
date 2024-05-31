# Protocol mapper group filter membership with regexp

*By please-open.it*

Ce projet a pour objet un mapper d'attributs utilisateur (groupes auxquels il appartient) en filtrant le nom des groupes remontés par REGEX.

## Installation dans Keycloak

```
mvn clean install
```

Copier le fichier JAR généré dans "deployment" vers le répertoire "providers" de Keycloak. Puis redémarrer le serveur Keycloak.

## Utilisation

Il est recommandé d'avoir des scopes "client scopes" dédiés pour cet usage. 

Ajouter un mapper par "Add Mapper" puis "by configuration".

![alt text](image.png)

La configuration est identique au mapper de groupes de Keycloak à l'exception du champ "regexp" : 

![alt text](image-1.png)

Le module utilise "Pattern" : https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html