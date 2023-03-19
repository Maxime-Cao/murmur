# Murmur Server

## Information
Le murmur server est un projet créé avec Gradle. Il est donc nécessaire d'avoir Gradle installé sur votre machine pour pouvoir lancer le projet.<br>
```
Version gradle : 7.6 
Version java : 17
```
Lien téléchargement : 
<br>[GRADLE](https://gradle.org/install/)
<br>[JAVA](https://www.oracle.com/be/java/technologies/downloads/#jdk17-windows)

## Classe du projet
```
ClientRunnable : Thread d'un client

Protocol : Grammaire

MulticastRunnable : Thread du multicast
RelayManager : Gestionnaire de relais
RelayRunnable : Thread d'un relais

ServerFactory : Fabrique de serveur (lancement ServerManager & RelayManager)
ServerManager : Gestionnaire de serveur
TLSSocketFactory : Fabrique de socket TLS

TaskExecutor : Exécuteur de tâches
TaskManager : Gestionnaire de tâches

AESCodec : Codec AES (chiffrement / déchiffrement)
NetChooser : Choix de l'interface réseau
```
## Lancement du projet
```java
Lancement classique :
gradle run
./gradlew run

*Configuration par défault*
JSON : server1.json
CERTIFICAT : star.godswila.guru.p12 | labo2023
````
```java
Lancement avec un fichier de configuration :
gradle run --args="-c server1.json"
./gradlew run --args="-c server1.json"
```
```java
Lancement avec un fichier de configuration et un certificat :
gradle run --args="-c server1.json -f star.godswila.guru.p12"
./gradlew run --args="-c server1.json -f star.godswila.guru.p12"
```
```java
Lancement avec un fichier de configuration, un certificat et un mot de passe :
gradle run --args="-c server1.json -f star.godswila.guru.p12 -p labo2023"
./gradlew run --args="-c server1.json -f star.godswila.guru.p12 -p labo2023"
```

## Bibliothèque externe
[GSON](https://github.com/google/gson)
