### How to use this spring-boot project

- Install packages with `mvn package`
- Run `mvn spring-boot:run` for starting the application (or use your IDE)

Application (with the embedded H2 database) is ready to be used ! You can access the url below for testing it :

- Swagger UI : http://localhost:8080/swagger-ui.html
- H2 UI : http://localhost:8080/h2-console

> Don't forget to set the `JDBC URL` value as `jdbc:h2:mem:testdb` for H2 UI.



### Instructions

- download the zip file of this project
- create a repository in your own github named 'java-challenge'
- clone your repository in a folder on your machine
- extract the zip file in this folder
- commit and push

- Enhance the code in any ways you can see, you are free! Some possibilities:
  - Add tests
  - Change syntax
  - Protect controller end points
  - Add caching logic for database calls
  - Improve doc and comments
  - Fix any bug you might find
- Edit readme.md and add any comments. It can be about what you did, what you would have done if you had more time, etc.
- Send us the link of your repository.

#### Restrictions
- use java 8


#### What we will look for
- Readability of your code
- Documentation
- Comments in your code 
- Appropriate usage of spring boot
- Appropriate usage of packages
- Is the application running as expected
- No performance issues

#### What I have done
- Added helper functions to create and Compare Entity objects, which helped writing unit tests without having to modify the underlying class. 
- Added Unit Tests for the Controller and Services layers, this is useful to easily identify issues we may find on each layer.
- Fixed a bug that was identified by running the unit tests, in which the POST endpoint was not properly wired to the body of the request.

#### With more time and a bit more of intestigation I would have liked to do also
- Refactor the Unit Tests to avoid repetition of code and move it into a "utils" module.
- Add integration tests to identify code leaks that could have impact on other modules, for this small project there was not much need for it but if the project were larger it would be useful to have.
- Add protection to the endpoints by adding middlewares to verify origin, authorization, etc. of the API calls.
- Increase test cases to cover some limit cases like having null pointers, references to inexistent records in DB, etc.
