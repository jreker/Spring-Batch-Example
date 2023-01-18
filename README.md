# Spring Batch Example
This is a simple example of how to use spring batch with Chunks and one Tasklet Step.

## Description 
This spring batch application reads all entries from the "verkehrsstraftaten.csv" file in the java resource folder.
It filters all entries lower than 100€ via the item processor. The last step is the write into the mariadb.

## Run
Start Database container from the folder "Docker"
```
docker-compose up
```
## Sample Data
The source of the sample data is GOVDATA:
https://www.govdata.de/web/guest/suchen/-/details/bussgelder-fliessender-verkehr-2021-der-stadt-aachen

