# Fakestore API Tests (JUnit5 + RestAssured)

This project contains automated API tests for https://fakestoreapi.com using **JUnit 5** and **RestAssured**.

## Project Structure

Domain-specific test data is grouped by API area (Products, Users, Carts, Auth), with matching endpoint constants.

- `src/test/java/com/qalab/tests/`
  - Test classes:
    - `ProductsApiTest`
    - `UsersApiTest`
    - `CartsApiTest`
    - `AuthApiTest`
- `src/test/java/com/qalab/data/`
  - Domain test constants + request payload builders:
    - `ProductsData`, `UsersData`, `CartsData`, `AuthData`
    - `CommonData` (e.g., default `BASE_URL`)
- `src/test/java/com/qalab/endpoints/`
  - Domain endpoint constants (and helpers):
    - `ProductsEndpoints`, `UsersEndpoints`, `CartsEndpoints`, `AuthEndpoints`
- `src/test/java/com/qalab/base/`
  - `BaseTest` (RestAssured base URI init + common spec fields)
- `src/test/java/com/qalab/utils/`
  - `SpecFactory` (shared RestAssured request/response specs)

## How to Run

### Run all tests

```bash
mvn test
```

### Run regression profile

```bash
mvn test -Pregression
```

## Configuration

### BASE_URL override

You can override the base URL at runtime:

```bash
mvn test -DBASE_URL=https://fakestoreapi.com
```

If `BASE_URL` is not set (or is empty), the tests fall back to the default defined in `CommonData`.

## Notes

- All request/response specs are created through `SpecFactory`.
- RestAssured and Allure are enabled for richer reporting.

