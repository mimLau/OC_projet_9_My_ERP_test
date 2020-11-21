# OC_Project_7 : MYERP_TEST 

  
  

## Table of contents 
* [Informations générales](#général) 
* [Technomogies utilisées](#technologies) 
* [La base de données](#bdd) 
* [Configuration du serveur d’intégration](#serveurIntegration) 


## Informations générales :  

### Nom de l'application  

MYERP_Test

### Description  

Système de facturation et de comptabilité pour un client. Vérification des différentes unités de fonctionnalité pour voir si l'application fonctionne correctement et qu'elle répond bien aux règles de gestion et les respecte.

Pour cela, mise en place :
- de tests unitaires 
- de tests d'intégration

### Organisation du projet

Le projet se compose de 4 modules:
- merp-business : pour lequel ont été mis en place des tests unitaires et tests d'intégration système
- myerp-consumer : pour lequel ont été mis en place des tests d'intégration système 
- myerp-model : pour lequel ont été mis en place des tests unitaires
- myerp-technical

### Organisation du répertoire

*   `doc` : documentation
*   `docker` : répertoire relatifs aux conteneurs *docker* utiles pour le projet
    *   `dev` : environnement de développement
*   `src` : code source de l'application



## Technologies utilisées : 

* Java 1.8  
* Maven 4.6 
* JUnit Jupiter 5.6.0
* JUnit 4.12
* JUnit Mockito 3.1.0
* Jacoco 0.8.5
* Jenkins 2.249.3
* Sonarcloud

## La base de données : 

Les composants nécessaires lors du développement sont disponibles via des conteneurs **docker**.
L'environnement de développement est assemblé grâce à **docker-compose**
(cf docker/dev/docker-compose.yml).

Il comporte :

*   une base de données **PostgreSQL** contenant un jeu de données de démo (`postgresql://127.0.0.1:9030/db_myerp`)

### Lancement

    cd docker/dev
    docker-compose up


### Arrêt

    cd docker/dev
    docker-compose stop


### Remise à zero

    cd docker/dev
    docker-compose stop
    docker-compose rm -v
    docker-compose up


## Configuration du serveur d’intégration : 


### Installation de Jenkins

Télécharger à partir du lien suivant https://www.jenkins.io/download/, la version de Jenkins correspondant à votre OS, l'installer, suiver les instructions indiquées,configurer le port sur lequel le serveur va se lancer (généralement port 8080) et valider. 

### Configuration de Jacco dans jenkins

Lancer Jenkins, puis se rendre dans **Manage jenkins**. Cliquer sur le menu **Manage Plugins**. Cliquer sur l'onglet **Available**, puis taper Jacoco dans le champs de recherche. Cocher le plugin Jacoco et cliquer sur installer. Une fois l'installtion terminée, redémarer Jenkin.

### Configuration du job des tests unitaires

Se rendre dans le menu Jenkins, cliquer sur **New Item**, donner un nom à votre job, dans notre cas **MYERP_UNIT_TESTS** et choisissez l'option **Maven projet** puis valider.

On arrive sur la page de configuration. Dans la partie **Général**, cochez **GitHub project** et taper dans le champs **Project url**, l'url du dépot du projet (https://github.com/mimLau/OC_projet_9_MyERP_test.git). 
Dans **Source Code Management**, cliquer sur **Git** et coller à nouveau l'url du dépot du projet dans le champs Repository URL.

Dans la partie **Build** dans le champs **Root Pom**, tapez src/pom.xml.
Puis dans le champs **Goals and option**, tapez **test**.

Enfin, dérouler le menu **Add post-build action**, **Record Jacoco coverage report**, puis valider la configuration. 
Lancer un job, en cliquant dans le menu à droite sr **Build job**.


### Configuration du job des tests d'intégration

Procéder de la même facon que pour les tests unitaires. 
Créer un nouveau item pour les tests d'intégration du module myerp-business et un pour le module myerp-consumer.
Dans le champs **Goals and option**, cettte fois-ci, taper **clean verify -P test-business -pl myerp-business** pour les tests d'intégration du module myerp-business, et **clean verify -P test-consumer -pl myerp-consumer** pour les tests d'intégration du module myerp-consumer.

Puis dans **Record Jacoco coverage report**, dans le champs **Path to exec files** taper  __**/jacoco-integration-tests.exec__ , puis valider.
Enfin, cliquer sur **Buil job**, pour chacun des jobs créés.