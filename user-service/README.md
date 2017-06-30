# UserStory 1 : creating users

During TDD, we don't speak about restcontroller, persistence etc.
The first steps consist in create service layer which is not linked to Spring (hexagonal architecture)

### 1. Creating User model

The very first step consists in creating the User model {id, login, password, role (defined by an enum)}

### 2. UserService allow to create a user

* Creating a user implies to store it somewhere.
* Creating a user implies to increment its id
* Creating a user implies to be able to retrieve it (by its id)

This step consists in writting a test to ensure that, **without checking any role**, a user is able to create another user
* UserService will be created, without any interface.
** for the moment, the persisting concern is not addressed --> a list is created
** both methods "create" and "get" are required --> so they are written

### 2. Authorisation : only users with role "ADMIN" are able to create users.

This step consists in writting two kind of tests for authorization
* One test to check an administrator is able to create and to retrieve a user
* One test to check a non administrator is not able to create nor retrieve a user

A decorator could be a good solution because it will add functionnalities (=authorisation check) without updating anything
For that reason an interface is needed.

### 3. Repository

Storing users in a list is good for starting but obviously not the target. But instead of implementing persistence into database, we could use an abstraction
Moreover, it will be compatible with hexagonal architecture.
* Interface IUserRepository is created ans pass through UserService constructor
* The previous implementation with the list can be reused for the Mock

### 4. Behavior testing

Now it is time to create the cucumber feature in order to test behavior.