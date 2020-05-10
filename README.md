# integrationTests
Ici se trouvent les tests d'intégration de toute la chaîne de drone delivery (incluant donc les 3 parties J2E, DotNet et CLI) afin de garantir que toute la chaîne est fonctionnelle et stable.
Pour lancer ces tests, lancer les serveurs docker avec `docker-compose up -d` en se placant dans le répertoire docker à la racine du projet, puis faire un `mvn clean package` à la racine de ce repository pour lancer les tests.

