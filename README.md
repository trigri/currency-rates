# currency-rates
Fetch and show currency rates and update every second.

## Task:
List all currencies you get from the endpoint (one per row). Each row has an input where you can enter any amount of money. When you tap on a currency row it should slide to the top and it's input becomes the first responder. When you’re changing the amount the app must simultaneously update the corresponding value for other currencies.

## Solution

Fetch the currencies with rates from the end point each second. 

|Currency|Rate|
|---|---|
|AUD|1.595|
|BGN|1.977|
|BRL|4.221|
|CAD|1.143|
|CHF|7.737|
|CNY|25.96|
|DKK|7.529|
|GBP|0.887|
|HKD|25.96|
|...|...|

Convert response in a way to use in the presentation and show list of currency rates in a list and update every second. 

## Project Structure

Project uses MVVM appoach. Dagger2 is used for dependency injection. Retrofit is used for networking along with RxJava for reactive programming and other libraries. It has two modules **app** and **data**.

###### :app
Handles all the presentaion logic it receieves data and draws on the views.

###### :data
Data fetches the data from the server using and passes it to presentaion. 

# Requirements
- Android SDK 19 or above
- Android Studio 3.6
- Kotlin 

# Setup
- Clone or download the project
- Navigate to the project directory
- Use emulator or device to run the app
# Screenshots

|Loading State| With Data|
|---|---|
|![Screenshot_20200415-181936](https://user-images.githubusercontent.com/8000799/79355645-9ad04480-7f46-11ea-8040-9593d14e1173.png)|![Screenshot_20200415-181606](https://user-images.githubusercontent.com/8000799/79355576-868c4780-7f46-11ea-8a5e-53677748fd51.png)|

# Known issues
- When user selects new currency in the list for one iterations old currency rates are shown



