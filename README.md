# PRIORITY QUEUE CODING ASSIGNMENT

## Design/Implementation considerations

Initially, I had an idea to use something like a priority queue, and an additional Map to keep IDs. However, because priorities are non-decreasing functions of time I decided to keep 4 separate queues  (`com.alvaria.test.pqueue.util.pqueue.OrderPriorityQueueImpl`), thus allowing me to avoid reconstructing them periodically. But this did not help to avoid O(N) complexity when searching by ID. I am still thinking about ideas:
- Use kinetic structures or similar (https://en.wikipedia.org/wiki/Kinetic_sorted_list, https://github.com/frankfarrell/kds4j)
- Use a sorted list and insert data according to current priorities, but change swap entries when one class becomes bigger than another class. For example, as some moment max(3; n log n) will be greater than n, max(4; 2n log n) will be greater than max(3; n log n). We can predict the moment in the future when it happens and adjust our list accordingly. It can be done either in a background thread or lazily, before each read.
- Another approach would be to keep a sort of read view after each merge operation and reconstruct this view when necessary


I assumed we have 3 important times:
- application start
- enqueue time
- current time
I applied the restriction that enqueue time bust be within the range between application start and current time. For unit tests I use non-real timestamps as we can't work in real-time.

Because I can effectively search for ID in the management queue I use `return orderPriorityTree.headSet(queueData, true).size() - 1;`
I hope it works effectively, the standard way to get rank information for a node is to use 
augmented trees: https://tildesites.bowdoin.edu/~ltoma/teaching/cs231/fall09/Lectures/10-augmentedTrees/augtrees.pdf. 
I found a sort of implementation: https://github.com/btrekkie/RedBlackNode but to take ownership of that code 
I will need to add plenty of unit test after refactoring, so using standards Java structures is safer.

For merge operation over 3 sorted lists I used a trivial technique, because we have only 3 of them (https://en.wikipedia.org/wiki/Merge_algorithm)

Controller tests `QueueControllerTest` could be more lightweight like with `MockMvc`, for example. 
I prefer to keep only validation/transformation logic in controllers.

In real PROD project I would use `ObectMaper` or similar to create response objects like
`QueueDataResponse`. The main idea is not to allow model POJOs directly for REST endpoints.

Date time format used in enqueing is:
`ddMMyyyy_HHmmss`
So REST call is:
http://localhost:8080/api/{id}/{dateTime}


For output another, more ISO based standard is used:
`yyyy-MM-dd HH:mm:ss`


## Run/Build project

- Build: ```mvn clean install```
- Run with local profile: ```mvn spring-boot:run -Dspring-boot.run.profiles=local```
"local" profile is expected to be used with local system development only.
Swagger and dev tools are disabled in prod profile

- Run integration tests: ```mvn clean verify -P integration-test```
It uses a light-weight Docker container `com.alvaria.test.pqueue.controller.QueueControllerTestIT`
- API documentation: run with local profile and navigate to http://localhost:8080/swagger-ui/#


## To fix:

- API documentation must be improved
- Tests must be refactored to match BDD (Given... When... Then...). Unit tests cannot fulfill this completely but still a good idea to align. The number of IT scenarios must be increased.
- The performance of returning a specific position by ID must be improved.
