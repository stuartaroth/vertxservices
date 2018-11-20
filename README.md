`vertxservices`

This project is just some practice learning a bit more about vertx.
It is not reflective of how you would want to deploy things in the future, since in its current form it is a monolith.
It however has allowed me to write separate HTTP REST services that can communicate through the vertx event bus or through HTTP calls.

The `BookVerticle` and the `MovieVerticle` function the same, with you being able to query using the query params of `creator` or `genre` to return a list of books or movies.

The `MediaVerticle` uses the vertx eventbus to query the `BookVerticle` and `MovieVertical` for genre, aggregating the calls, while the creator query param uses the HTTP client to communicate to both other services.
