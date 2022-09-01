# azure-cosmosdb-java

This repo contains a **sample CosmosDB SQL API application implemented in Java**.

It demonstrates the following:

- Use of a **Spring Boot** console application
- Use of the **Spring Data** framework with CosmosDB
- Use of the **CosmosDB Asynch SDK** 
- Use of the **CosmosDB Bulk Executor SDK**
- Use of the **Gradle** build tool rather than Maven
- Using a public domain Telemetry dataset - EPA Air Quality
- Use of a **Spring Boot** web application (future enhancement)

#### Chris Joakim, Microsoft, CosmosDB Global Black Belt (GBB)

---

## Links

- [Spring Boot](https://spring.io/projects/spring-boot) 
- [Spring Data](https://spring.io/projects/spring-data)
- [Spring Data CosmosDB](https://docs.microsoft.com/en-us/azure/cosmos-db/sql/sql-api-sdk-java-spring-v3)
- [CosmosDB Java SDK v4](https://docs.microsoft.com/en-us/azure/cosmos-db/sql/sql-api-sdk-java-v4)
- [CosmosDB Java SDK v4 JavaDocs](https://azuresdkdocs.blob.core.windows.net/$web/java/azure-cosmos/latest/index.html)
- [CosmosDB Java SDK v4 Samples](https://github.com/Azure-Samples/azure-cosmos-java-sql-api-samples)
- [CosmosDB Java SDK v4 Best Practices](https://docs.microsoft.com/en-us/azure/cosmos-db/sql/best-practice-java)
- [CosmosDB Java SDK v4 Performance Tips](https://docs.microsoft.com/en-us/azure/cosmos-db/sql/performance-tips-java-sdk-v4-sql?tabs=api-async)
- [CosmosDB Java SDK @ GitHub](https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/cosmos/azure-cosmos)
- [CosmosDB Java Bulk Executor](https://docs.microsoft.com/en-us/azure/cosmos-db/sql/bulk-executor-java)
- [Gradle](https://gradle.org/)
- [azure-cosmos @ MvnRepository](https://mvnrepository.com/artifact/com.azure/azure-cosmos)

### Details

- [Spring Boot](https://spring.io/projects/spring-boot)


---

## Getting Started 

### Provision a CosmosDB SQL API Account

- Create a **CosmosDB SQL API Account**.  There are several ways to do this; including:
  - the **Azure Portal UI**, az CLI, ARM/Bicep, Azure DevOps, Terraform, etc..
- Create a **database** within the account; I use the name **dev**
- Create a **container** within the account; I use the name **telemetry** 
  - use the partition key **/pk** 
  - I provisioned **10,000 Request Units (RUs), Autoscale** for my container 
  
### Set Environment Variables 

Set the following environment variables so that the Java code can connect to 
your account and database.

```
AZURE_COSMOSDB_SQL_URI
AZURE_COSMOSDB_SQL_RW_KEY1
AZURE_COSMOSDB_SQL_DB
AZURE_COSMOSDB_SQL_MAX_DEG_PAR
```

**AZURE_COSMOSDB_SQL_URI** is the URI of your account and can be found in the
**Keys** view Azure Portal under your account.

**AZURE_COSMOSDB_SQL_RW_KEY1** is the primary read-write key, also found in the
**Keys** view Azure Portal under your account. 

**AZURE_COSMOSDB_SQL_DB** is the database name you created above.

**AZURE_COSMOSDB_SQL_MAX_DEG_PAR** can be set to 0.

### Clone This Repository and Compile the Code 

```
> git clone https://github.com/cjoakim/azure-cosmosdb-java.git
> azure-cosmosdb-java
> gradle build
```

#### Wait, what Libraries are we using, and what's on my CLASSPATH?

See **build.gradle** where you declare your project **dependencies**:

```
  dependencies {
      implementation 'com.azure.spring:spring-cloud-azure-starter-data-cosmos'    <---
      implementation 'org.apache.commons:commons-csv:1.9.0'
      compileOnly 'org.projectlombok:lombok'
      developmentOnly 'org.springframework.boot:spring-boot-devtools'
      annotationProcessor 'org.projectlombok:lombok'
      testImplementation 'org.springframework.boot:spring-boot-starter-test'
  }
```

The **CosmosDB SDK v4 library** is **azure-cosmos** and is included in
**com.azure.spring:spring-cloud-azure-starter-data-cosmos**.

To list the JAR files on the Gradle project CLASSPATH, run this command:

``` 
> gradle dependencies --configuration runtimeClasspath > data/classpath/runtimeClasspath.txt
```

Search file for **azure-cosmos** and you'll find:

```
|         +--- com.azure:azure-cosmos:4.32.0 -> 4.31.0
```

[CLASSPATH](console_app/data/classpath/runtimeClasspath.txt)

### Download the US EPA AirQuality Dataset

This dataset is from the **United States Environmental Protection Agency**
on **Air Quality**.  This public-domain dataset is used in this repo for **Telemetry** data.

See file **console_app/data/epa/readme.md** regarding how to download this data
since it is too large to store in GitHub.  It contains approximately 6.5 million rows.

After you download and unzip the file you should have this file relative to 
where you cloned this GitHub repository to:

```
console_app/data/epa/8hour_44201_2021/8hour_44201_2021.csv
```

Note: this file is ignored by git; see the .gitignore file.

### Review file application.properties

See file **console_app/src/main/resources/application.properties**
and make any necessary configuration edits.  

Notice how this file sets properties based on the environment variables
you set above.

```
# Spring Data CosmosDB
spring.cloud.azure.cosmos.endpoint=${AZURE_COSMOSDB_SQL_URI}
spring.cloud.azure.cosmos.key=${AZURE_COSMOSDB_SQL_RW_KEY1}
spring.cloud.azure.cosmos.database=${AZURE_COSMOSDB_SQL_DB}
```

### Load and Query CosmosDB 

Start in the root directory of the repository on your computer.

```
> cd .\console_app\
> mkdir tmp
> gradle build
```

See file **build.gradle** which defines "tasks" that Gradle can execute.
Note how the tasks can pass command-line arguments (args) to the Java program, 
like this:

```
task transformRawEpaOzoneData(type: JavaExec) {
    classpath = sourceSets.main.runtimeClasspath
    mainClass = 'org.cjoakim.cosmos.spring.App'
    args 'transform_raw_epa_ozone_data', '0', '50000', 'latLng', '--verbose'
}
```

#### Execute these Gradle tasks in this sequence

```
> gradle transformRawEpaOzoneData

> gradle loadTelemetryDataWithSpringData

> gradle queryTelemetryWithSpringData

> gradle queryTelemetryWithSdk 

> gradle deleteAllDocumentsWithSpringData 

> gradle loadEpaOzoneDataWithSdkBulkLoad
```

As the task names indicate, some of these tasks use **Spring Data** while others
use the **CosmosDB SDK** - this is intentional as a demonstration of each approach.
