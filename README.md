## Article Service

This is a SpringBoot based microservice written in Kotlin to allow a user to manage news Articles. This is only for demonstration purposes and as of now not ready for open use.

### In Scope
- Build basic CRUD endpoints to manage Articles.
- Ensure the endpoints follow REST specification.
- Handle basic validations (on create/update Article).
- Handle business exceptions and throw custom error messages.
- Support Pagination.
- Extensive testing (unit and integration).

### Out of Scope
- Authorization.
- Prevent creation of duplicate Articles.
- Exhaustive test coverage.
- Versioning of APIs
- Exhaustive validations.

## Development

### Choice of Technologies
- Kotlin
- SpringBoot
- MongoDB
- Junit 5 and Mockito
- Jackson (Serialization & Deserialization)
- Embedded Mongo (for testing)
- RandomBeansExtension for testing.

### Methodologies followed
- Most development was derived with tests first (**Test Driven Development**), however, there are parts that were written first without the tests. Some integration tests were added later.
- Focus was on writing simple and testable code, avoiding complexities and leave scope for extensions.
- Segregation of responsibility among classes. Classes like `SearchQueryBuilder` and extension functions like `MongoTemplate#find(query: Query, pageable: Pageable)` helps make the code look cleaner.
- Frequent logical commits (as much as possible)

### Known Limitation & Future Scope 
- Authorization is a must for such a service that exposes the endpoints to `CREATE`, `UPDATE` & `DELETE` the entities. These endpoints must be behind an `AuthMiddleware` and `SpringSecurity` could be used to setup a `RBAC` mechanism.
- `@CreatedAt` and `@UpdatedAt` fields in `Article` are the next must to have. Even an `EditedBy` that stores editor information (who created the Article) should be stored to control who can `UPDATE` an `Article`.
- The concept of `Editor` should be added.
- Currently, the results are not sorted (they could be sorted by `createdAt` date when added).
- The `DELETE` operation deletes an `Article` permanently and we can switch to soft-delete if needed.
- No validation exists yet to ensure that a duplicate `Article` can't be created. A logic needs to be introduced to avoid creation of duplicates by mistake.
- The `Article` entity should be split now to a separate DTO for request and response.
- Leaving non-nullable fields null while creating `Article` throws an ugly response message. This could be fixed once the DTOs are in place.  
- The test coverage is not exhaustive and classes like `ExceptionExt`, `GlobalExceptionHandler` and a few others are not unit tested. There are a few tests missing for some other scenarios as well. `ArticleControllerValidationTest` does not cover all the scenarios. 
- The `mockito` (and `mockito-kotlin`) library have limitations and it would be better to start using `Mockk`.

## How to Run & Test
- `Dockerfile` contains the setup to build image for article-service
- A `docker-compose` is provided to build containers for both `article-service` and `mongdb`.

Just ensure that docker is installed and docker daemon is up and running. Then switch to the project directory and execute:
```
docker compose up
```

Access using `http://localhost:8080/`

### Sample Request/Responses
- The sample requests are provided under `sample` folder in the root directory.
- Use an http-client ([like this](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html)) is the best way to test the sample requests and try out different scenarios.
- At the moment, authorization is not supported so no token is needed.