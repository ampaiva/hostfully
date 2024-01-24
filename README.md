# Alexandre Paiva
## Java Technical Test

## Overview
This repository contains a WebService that supports a REST API to handle bookings.

## Terminology
A booking is when a guest selects a start and end date and submits a reservation on a property.
A block is when the property owner or manager selects a range of days during which no guest can make
a booking (e.g. the owner wants to use the property for themselves, or the property manager needs to
schedule the repainting of a few rooms).

## Running the WebService

> [!IMPORTANT]
> Make sure you hava JAVA_HOME pointing to a proper JDK.

```
git clone git@github.com:ampaiva/hostfully.git
cd hostfully
./gradlew bootRun
```

## Running the tests

The tests cover all the requirements for the REST API.

```
./gradlew test
```


## REST API

The REST API allow users to:
- Create a booking
  - POST /api/bookings 
- Update booking dates and guest details
    - PUT /api/bookings/{id}
    - PATCH /api/bookings/{id}
    - PUT /api/guests
    - PATCH /api/guests
- Cancel a booking
  - PATCH /api/bookings/{id}/cancel
- Rebook a canceled booking
    - PATCH /api/bookings/{id}/rebook
- Delete a booking from the system
  - DELETE /api/bookings/{id}
- Get a booking
    - GET /api/bookings/{id}
- Create, update and delete a block
    - POST /api/blocks
    - PUT /api/blocks/{id}
    - PATCH /api/blocks/{id}
    - DELETE /api/blocks/{id}
