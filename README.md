# Introduction

We are using [gatling](https://gatling.io/) to perform the load tests to TaxService.

### Pre-requesite
It's required to have Java installed in the machine

### Configuring Execution
1. Refer to `performance-script\user-files\resources\parameters.json` file
2. Enter the parameters of the Service that will be tested

`Duration`: The duration in minutes of the execution  
`Users`: The number of concurrent users  
`Items`: The number of line items that will be used in the payload  
`Endpoint`: The endpoint of the post request e.g. `"/quote"`  
`URL`: The URL of the service  
`Token`: The bearer token  
`Req/m`: The max req/m that should be throtled, use 0 for infinite

### Running the tests
1. In the command line `cd` to the `performance-scripts\bin\` folder
2. In the Windows OS, run the `gatling.bat`
3. Select the load test you want to execute by typing its number
4. The results will be available in the `results` folder

## Test Scenario
Check out our [test scenarios](https://github.wdf.sap.corp/TaaS/performance-scripts/tree/master/user-files/simulations/sap).

### Creating Test Scenarios
1. Create a new `scala` class in a file placed under the `user-files\simulations\sap\`
2. Ensure your class extends the `Simulation` class
3. Once you save it, it will be available to be executed

You can find more information about [gatling API](https://gatling.io/docs/current/cheat-sheet/).

