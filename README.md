# azure-cosmosdb-java

This repo contains a **sample CosmosDB SQL API application implemented in Java**.

It demonstrates the following:

- Use of a **Spring Boot** console application
- Use of the **Spring Data** framework with CosmosDB
- Use of the **CosmosDB Asynch SDK** 
- Use of the **CosmosDB Bulk Executor SDK** 
- Use of a **Spring Boot** web application (future enhancement)
- Use of the **Gradle** build tool rather than Maven

#### Chris Joakim, Microsoft, CosmosDB Global Black Belt (GBB)

---

## Links

- [Spring Boot](https://spring.io/projects/spring-boot) 
- [Spring Data](https://spring.io/projects/spring-data)
- [Spring Data CosmosDB](https://docs.microsoft.com/en-ca/azure/cosmos-db/sql/sql-api-sdk-java-spring-v3?tabs=explore)
- [CosmosDB Java SDK v4](https://docs.microsoft.com/en-us/azure/cosmos-db/sql/sql-api-sdk-java-v4)
- [CosmosDB Java Bulk Executor](https://docs.microsoft.com/en-us/azure/cosmos-db/sql/bulk-executor-java)
- [Gradle](https://gradle.org/)
- [Maven Central](https://search.maven.org/)

### Dataset: United States Environmental Protection Agency Air Quality

This dataset is used in this repo for **Telemetry** data. 


- https://aqs.epa.gov/aqsweb/airdata/download_files.html#eighthour
- https://aqs.epa.gov/aqsweb/airdata/8hour_44201_2021.zip 
  File: 8hour_44201_2021.zip, 6,554,736 Rows, 55,243 KB, As of 2022-06-01

---

## Getting Started 

### Provision a CosmosDB SQL API Account

- Create a **CosmosDB SQL API Account**
  - There are several ways to do this; including:
    - The Azure Portal UI 
    - PowerShell
    - az CLI
    - Bicep 
    
- Create a **database** within the account; I use the name **dev**
- Create a **container** within the account; I use the name **telemetry** 
  - use the partition key **/pk** 
  - I provisioned **10,000 Request Units (RUs), Autoscale** for my container 
  
### Set Environment Variables 

```

```
### Clone This Repository and Compile the Code 

```
> git clone https://github.com/cjoakim/azure-cosmosdb-java.git
> azure-cosmosdb-java
```

### Download the EPA Data 

See file **console_app/data/epa/readme.md** regarding how to download this data
since it is too large to store in GitHub.

After you download and unzip the file you should have this file relative to 
where you cloned this GitHub repository to:

```
console_app/data/epa/8hour_44201_2021/8hour_44201_2021.csv
```

Note: this file is ignored by git; see the .gitignore file.

### Load and Query CosmosDB 



