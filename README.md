# CGAT-PDE User Management   
Micro service providing services for user management within CGAT-PDE application. It plays crucial role in handling user-related functions including user creation, deactivation and managing roles and permissions. It interacts with OKTA to ensure secure authentication.

## Table of Contents
1. [Tools Required](#tools-required)
2. [Pull Requests](#pull-requests)
3. [Compiling and running the Service](#compiling-and-running-the-service)
4. [Postman Configuration](#postman-configuration)

## Tools Required

1. [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
2. [Maven](https://maven.apache.org/download.cgi)
3. [Postgresql](https://www.postgresql.org/download/)
4. [Docker](https://www.docker.com/products/docker-desktop/)
5. [JBAT Parent](https://github.com/mckesson/jbat-parent/) ---> Need to review this
6. [JBAT Modules](https://github.com/mckesson/jbat-modules/) ---> Need to review this

## Pull Requests

We create feature branches and then create a pull request off that branch, after approval the code is merged.
```console
git checkout -b feature/ikmr-XXXXX
```
Make changes...
```console
git add <files-changed>
git commit -m "Feature IKMR-XXXXX"
git pull --rebase origin main`
git push -u origin feature/ikmr-XXXXX
```

## Compiling and running the Service

```console
make clean build deploy
```

## Postman Configuration
1. Import the collection json files from `postman` dir. 
2. Edit imported collection variables for your setup.
3. Execute the required APIs. You should get a successful response back from the service.
