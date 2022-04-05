Design/Implementation considerations


Not done yet:
API documentation must be improved
Tests must be refactored to match BDD (Given... When... Then...). Unit tests cannot fulfill this completely but still a good idea to align. The number of IT scenarios must be increased.
To improve the performance of returning a specific position by ID it would be good to use kinetic structures or similar (https://en.wikipedia.org/wiki/Kinetic_sorted_list, https://github.com/frankfarrell/kds4j). This will help to advance the structure when the time goes on


Run/Build project:
Build: mvn clean install
Run with local profile: mvn spring-boot:run -Dspring-boot.run.profiles=local
"local" profile is expected to be used with local system development only. Swagger and dev tools are disabled in prod profile
Run integration tests: mvn clean verify -P integration-test
It uses a light-weight Docker container com.alvaria.test.pqueue.controller.QueueControllerTestIT
API documentation
Execute with local profile and navigate to http://localhost:8080/swagger-ui/#