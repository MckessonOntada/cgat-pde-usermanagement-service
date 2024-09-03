clean:
	mvnw clean
build:
	mvnw package spring-boot:repackage
deploy:
	nohup java -jar target/cgat-pde-usermanagement-service.jar &