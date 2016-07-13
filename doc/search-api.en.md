## Search API

### Brief introduction

Ticwear open data interface searches more than 60 vertical fields, to facilitate developers to leverage existing data for application development.

In order to call the API, we need to create a MobvoiApiClient instance, as the entrance to the API call.

``` java
MobvoiApiClient mClient = new MobvoiApiClient.Builder(this)
        .addConnectionCallbacks(new ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle connectionHint) {
                Log.d(TAG, "onConnected: " + connectionHint);
                // Now you can use the API
            }
            @Override
            public void onConnectionSuspended(int cause) {
                Log.d(TAG, "onConnectionSuspended: " + cause);
            }
        })
        .addOnConnectionFailedListener(new OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult result) {
                Log.d(TAG, "onConnectionFailed: " + result);
            }
        })
        // Request access only to the Search API
        .addApi(Search.API)
        .build();
```

You need to call `connect()`, and wait until after `onConnected()` for the callback client to work properly.

Before calling, make sure that the phone has Ticwear mobile companion. To check the weather in Beijing, for example, the search API call code is as follows:

``` java
OneboxRequest request = new OneboxRequest(OneboxTask.WEATHER);
request.params.add(new ParamItem("location", "北京"));
PendingResult<OneboxResult> result = Search.OneboxApi.fetchOneboxResult(request);
result.setResultCallback(new ResultCallback<OneboxResult>() {

    @Override
    public void onResult(OneboxResult result) {
        if (result.getStatus().isSuccess()) {
            Log.d("MainActivity", result.getResponse().toString());
        } else {
            Log.d("MainActivity", "Fail to get the response");
        }
    }
});
```

`OneboxRequest` is based on two request data properties, task and params.  task is to distinguish between the requested data types that are defined in the OneboxTask.  params is a parameter, the filtering criteria for the defined request.  By default uses the current local address for search.  For example, the following code example is to search for nearby restaurants:

``` java
 OneboxRequest request = new OneboxRequest(OneboxTask.RESTAURANT);
 PendingResult<OneboxResult> result = Search.OneboxApi.fetchOneboxResult(request);
 result.setResultCallback(new ResultCallback<OneboxResult>() {

      @Override
      public void onResult(OneboxResult result) {
        if (result.getStatus().isSuccess()) {
            Log.d("MainActivity", result.getResponse().toString());
        } else {
            Log.d("MainActivity", "Fail to get the response");
        }
    }
});
```

### Common data types
  * Weather
   * Request parameters
     * OneboxTask.WEATHER
     * location: Query Region ( "Beijing", "Haidian District", etc.)
   * Return value
     * maxTemp: Highest temperature
     * minTemp: Minimum temperature
     * weekDay: Day of the week
     * currentTemp: Current Temperature
     * pm25: pm 2.5 Index
     * tips: Weather alerts
     * weather: weather (cloudy, overcast ...)
  * Restaurants
   * Request parameters
     * OneboxTask.RESTAURANT
     * city: City
     * location: Query Region ( "Zhongguancun", "Wudaokou", etc.)
     * category: Category ( "Korean", "Cantonese", etc.)
     * name: Restaurant name ( "Hot Temptation", "food stalls in Nanjing," etc.)
     * price: Price range ( "1-100" indicates 1-100 yuan, etc.)
     * distance: Distance ( "500" means that less than 500 m)
   * Return value
     * address: Address
     * distance: Distance
     * endPoint: Coordinates
     * phone: Phone number
     * score: Rating
     * title: Restaurant name
     * linkUrl: Details link
  * Movies
   * Request parameters
     * OneboxTask.CINEMA
     * city: city
     * location: Query Region ( "Zhongguancun", "Wudaokou", etc.)
     * movie: Movie name
     * type: Please fill as "movie"
   * Return value
     * director: Director
     * length: Duration
     * linkUrl: Details link
     * phone: Phone number
     * score: Rating
     * name: Name of the movie
     * stars: Starring actors
  * People nearby
   * Request parameters
     * OneboxTask.BEAUTY
     * city: city
     * gender: gender ( "male", "female")
     * constellation: the constellation ( "Scorpio", "Aries", etc.)
   * Return value
     * nameZh: Nickname
     * introduction: Introduction
     * linkUrl: Details link
  * Cinemas
   * Request parameters
     * OneboxTask.CINEMA
     * city: City
     * location: Query Region ( "Zhongguancun", "Wudaokou", etc.)
     * name: Name of the theater
     * type: Please fill out "cinema"
   * Return value
     * address: Address
     * distance: Distance
     * endPoint: Coordinates
     * linkUrl: Details link
     * title: Theater name
  * Hotels
   * Request parameters
     * OneboxTask.HOTEL
     * city: City
     * location: Query region ( "Zhongguancun", "Wudaokou", etc.)
     * name: Name of the hotel
     * hotel_type: Star ( "three-star hotel", "four-star hotel," etc.)
   * Return value
     * address: Address
     * linkUrl: Details link
     * distance: Distance
     * endPoint: Coordinates
     * phone: Phone
     * price: Price
     * score: Rating
     * title: Hotel name
  * Places nearby
   * Request parameters
     * OneboxTask.POI
     * city: City
     * location: Query region ( "Zhongguancun", "Wudaokou", etc.)
     * name: Name
     * category: Category ( "shop", "sauna", etc.)
   * Return value
     * address: Address
     * linkUrl: Details link
     * distance: Distance
     * endPoint: Coordinates
     * phone: Phone
     * title: Name
  * Translation
   * Request parameters
     * OneboxTask.TRANSLATE
     * content: content
     * target: the target language ( "English", "Japanese", etc.)
   * Return value
     * content: translations
  * Yellow Pages
   * Request parameters
     * OneboxTask.YELLOWPAGE
     * city: City
     * location: Query region ( "Zhongguancun", "Wudaokou", etc.)
     * name: Name
     * category: Category ( "shop", "sauna", etc.)
   * Return value
     * content: Phone
  * Navigation
   * Request parameters
     * OneboxTask.NAVIGATION
     * from_city: Departure City
     * to_city: Arrival City
     * from: Departure
     * to: Destination
     * from_point: starting point coordinates (format: latitude, longitude, for example: "39.9897,116.316")
     * to_point: arrival point coordinates (format: latitude, longitude, for example: "39.9897,116.316")
     * type: public transport, walking, driving
   * Return value
     * distance: Distance
     * duration: Duration
     * price: Price of a taxi
     * startPoint: Departure coordinates
     * endPoint: Destination coordinates
     * routes: Navigation path
  * Trains
   * Request parameters
     * OneboxTask.TRAIN
     * from: Departure
     * to: Destination
     * departure_date: Departure date (in the format: yyyy-MM-dd, for example: 2015-06-03)
     * train_number: train number
     * train_type: type ("Express", "high-speed rail," etc.)
   * Return value
     * startTime: Departure time
     * duration: Duration
     * seats: Prices and more than ticket information
     * startStation: Departure station
     * endStation: Arrival station
     * number: train number
     * endTime: Arrival Time
  * Flights
   * Request parameters
     * OneboxTask.FLIGHT
     * from: Departure
     * to: arrival
     * airline: Airlines ( "China Southern Airlines", etc.)
     * departure_date: Departure date (in the format: yyyy-MM-dd, for example: 2015-06-03)
   * Return value
     * startTime: Departure time
     * endTime: Arrival Time
     * price: Price
     * flightNo: Flight No
     * linkUrl: Details link
     * from: Departure
     * to: Destination
     * discount: Discount
  * Flight information
   * Request parameters
     * OneboxTask.FLIGHT_INFO
     * number: Flight No
   * Return value
     * arrExpected: Planned arrival
     * arrActual: Actual arrival
     * depExpected: Planned departure
     * depActual: Actual departure
     * status: Flight status
     * name: Flight number
     * date: Date
     * from: Departure
     * to: Destination
  * Jokes
   * Request parameters
     * OneboxTask.JOKE
   * Return value
     * content: content
