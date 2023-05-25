# Toucher

Toucher is a travelcard system simulation like [London's Oyster card](https://en.wikipedia.org/wiki/Oyster_card). It takes a CSV file that contains touches, processes the content and produces three output files including successful trips, unprocessable trips, and trip summary.

### 1) Hypothetical travel system

#### Key Concepts:

+ Touch On: On boarding a bus, passengers taps their credit card (identified by a Hashed Number called as Primary Account Number) which is called as Touch On.
+ Touch Off: When passenger gets off the bus, they tap their card again which is called a Touch Off.
+ Amount to Charge: The amount to charge the passenger will be determined where they Touch On and where the Touch Off. The amount is determined as follows:
  + Trip Between Stop A and Stop B costs $4.50
  + Trip Between Stop B and Stop C costs $6.25
  + Trip Between Stop A and Stop C costs $8.45
+ Travel Direction:  The above Amount to Charge applies to travel in either direction. This means that the same amount is charged if a passenger Touch On at Stop A and Touch Off at Stop B OR they can Touch On at Stop B and Touch Off at Stop A.

#### Types of Trips

+ Completed Trips: If the passenger Touch On at one stop and Touch Off at another stop, this is treated as a completed trip. The amount to charge the passenger is determined by the above Amount to Charge section. E.g: Touch On at Stop A and Touch Off at Stop C is a completed trip and passenger is charged $8.45.
+ Incomplete Trips: If the passenger Touch On at one stop and forget to Touch Off at another stop, this is treated as an incomplete trip. The passenger in this case is charged the maximum possible fare, where they could have travelled to. Eg: A passenger Touch On at Stop B and does not Touch Off, they could travelled to either Stop A ($4.5) or Stop C ($6.25). In this case, they will be charged the higher value ($6.25).
+ Cancelled Trip: If the passenger Touch On and Touch Off at the same stop, this is called a cancelled trip and the passenger would not be charged.

### 2) Problem scenario

Given an input file `touchData.csv` in CSV format containing the Touch On and Touch Off data per line, the app will produce 3 output files as defined below.

<img src="https://github.com/emeraldhieu/toucher/blob/master/images/toucher.png" width="90%">

`touchData.csv`
```
ID, DateTimeUTC, TouchType, StopID, CompanyID, BusID, PAN
1,16-05-2023 12:15:00, ON, StopA, Company1, Bus10, 2255550000666662
2,16-05-2023 12:25:00, OFF, StopB, Company1, Bus10, 2255550000666667
3,16-05-2023 12:28:00, ON, StopB, Company2, Bus11, 2255550000336667
4,16-05-2023 12:45:00, OFF, StopC, Company2, Bus11, 2255550003366668
5,16-05-2023 12:55:00, ON, StopB, Company1, Bus25, 2255550000667767
```

Touch On and Touch Off will be matched to create trips. How much to charge for the trip is calculated based on whether it was complete, incomplete or cancelled and where the Touch On and Touch Off occurred. The PAN should be SHA256 hashed.

`trip.csv`
```
"Started","Finished","DurationSec","FromStopId","ToStopId","ChargeAmount","CompanyId","BusId","HashedPan","Status"
"16-05-2023 12:15:00","16-05-2023 12:25:00",600,"StopA","StopB",4.5,"Company1","Bus10","e34140faa1e4f3a823399e23a73f6fea0034e6306f98f5437f01ed4a1b670d91","COMPLETED"
"16-05-2023 12:28:00","16-05-2023 12:45:00",1020,"StopB","StopC",6.25,"Company2","Bus11","1cceb46c4b56c2264527990d09caf401f3b2b4e529c9529b78ab0b9ce9be9264","COMPLETED"
"16-05-2023 12:55:00",,,"StopB",,6.25,"Company1","Bus25","1252f059a062a1c5baac8cdc67b086817be23ec7cd0bbfad7bd7a19ec863a67e","INCOMPLETE"
```

Any touch that could not be processed will need to be written out to a file `unprocessableTouchData.csv` along with the reason they could not be processed (e.g. missing data, invalid data, duplicate touch etc.) The PAN should be 256 Hashed.

```
"Started","Finished","DurationSec","FromStopId","ToStopId","ChargeAmount","CompanyId","BusId","HashedPan","Status"
"16-05-2023 12:15:00","16-05-2023 12:25:00","StopA","StopB",4.5,"Company1","Bus10",,"PAN was missing"
```

A summary of the trips will be written to a file called `tripSummary.csv`. The file would contain the number of complete, incomplete and cancelled trips along with total charges, sorted and grouped by Date, CompanyId and BusID.

```
"Date","CompanyId","BusId","CompleteTripCount","IncompleteTripCount","CancelledTripCount","TotalCharges"
"16-05-2023","Company1","Bus10",2,0,1,6.5
```

#### Assumptions
+ Touch ON has to happen before Touch OFF in case of the same date, companyId, and busId
+ Summary's date is start date aka started
+ Charset of the input file is UTF-8
+ CSV delimiter is comma
+ Hashed PAN is created by the formula `Hash256(PAN of fromStop + "_" + PAN of toStop)`

### 3) Quickstart

#### Prerequisites

Java17, Maven, Docker Desktop

#### Run the app

Check out the repo. At the project directory, build the app
```sh
mvn clean install
```

Dockerize and start the app container
```sh
docker compose up -d
```

Process the file
```sh
curl --location 'localhost:50001/files' \
--form 'file=@"files/touchDataAllCases.csv"' > result.zip
```

The output will be a ZIP file. Unzip it.

```sh
unzip result.zip -d result
```

Well done! Check the files `trip.csv`, `unprocessableTouchData.csv`, and `tripSummary.csv`.

### 4) Technical details

+ The app uses [Jackson Dataformat CSV ](https://github.com/FasterXML/jackson-dataformats-text/tree/2.16/csv) to parse the CSV input file
+ To determine a pair of touches ON and OFF, a composite key of date, companyId, and busId is used for comparision
  + [A color-highlighted XLSX file](files/touchDataAllCases.xlsx) is included for you to see exactly which lines are paired
+ New routes with charges can be added to `RouteProvider`. For instance, "StopD to StopC costs 42$".
+ The programming approach is TDD. New cases can be covered by new unit tests.
