# Alexandre Paiva
## Java Techinical Test

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
- Update booking dates and guest details
- Cancel a booking
- Rebook a canceled booking
- Delete a booking from the system
- Get a booking
- Create, update and delete a block
