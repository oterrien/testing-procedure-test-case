# User-Service

http://localhost:8080/swagger-ui.html

During TDD, we don't speak about restcontroller, persistence etc.
The first steps consist in creating  service layer which is not linked to any framework (hexagonal architecture)

## Creating User model

The very first step consists in creating the User model {id, login, password, role (defined by an enum)}

## UserService allows to create and retrieve a user

* Creating a user implies to store it somewhere.
* Creating a user implies to increment its id
* Creating a user implies to be able to retrieve it (by its id)

This step consists in writting a test to ensure that, **without checking any role**, a user is able to create another user
* UserService will be created, without any interface.
** for the moment, the persisting concern is not addressed --> a list is created
** both methods "create" and "get" are required --> so they are written

## Authorisation: only users with role "ADMIN" are able to create and retrieve users.

This step consists in writting two kind of tests for authorization
* One test to check an administrator is able to create and to retrieve a user
* One test to check a non administrator is not able to create nor retrieve a user

A decorator could be a good solution because it will add functionnalities (=authorisation check) without updating anything
For that reason an interface is needed.

## Repository: UserService don't care how users are persisted

Storing users in a list is good for starting but obviously not the target. But instead of implementing persistence into database, we could use an abstraction
Moreover, it will be compatible with hexagonal architecture.
* Interface IUserRepository is created ans pass through UserService constructor
* The previous implementation with the list can be reused for the Mock

## Behavior testing

Now it is time to create the cucumber feature in order to test behavior.
* Admin are able to create and retrieve any user (any role)
* Non Admin are not able to create nor retrieve a user

## Hexagonal Architecture

* Move IUserService interface into "**api**" package. That is the contract of the hexagon
* Move IUserRepository interface into "**spi**" package. That is how
* Move UserService and AuthorizationUserService into business.

## Convert into Micro-Service

https://gist.github.com/thomasdarimont/8d6bc243d3b504439e67d57cb0d0bb72