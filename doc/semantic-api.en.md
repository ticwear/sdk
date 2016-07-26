## Semantic API

### Brief introduction

`Semantic Intent` is Ticwear’s semantics open interface. As a developer through this interface and only a few extra lines of code, you can handle user input voice commands. 

Semantic definition is meaning in language or logic. Intent means that Android `Activity/Service/Broadcast` interact with each other.  When the user speaks a voice command on the main watch interface (eg: Weather in Shanghai Monday), the voice will be uploaded to our background industry-leading speech recognition and semantic analysis system, and then an accurate analysis of user intent will return to watch the front end. The watch front end will take intention, and pack it into a Semantic Intent to send to third-party applications, with its action to indicate the type of intent (in this case is the action = check the weather), and add some of the semantic analysis semantic tags as Intent Extras (in this case is time = Wednesday, location = Shanghai). 

 As a third-party developer, in your App `AndroidManifest.xml` file you can affirm which `Activity` can handle which corresponding Action. The system will automatically start your App at the corresponding `Activity`, then you only need to process the incoming Intent. Using our API to parse this Intent, you will receive the semantic analysis results as semantic tags. 

### Steps
 
Next, we have an example, that takes you step by step to build a App, which receives and processes flight search Semantic Intent.  We assume you already have Android application development experience.  First generate your own Ticwear App application, it contains an `Activity`. 

1) Affirm Intent Filter in AndroidManifest.xml

In your application's AndroidManifest.xml in the corresponding `Activity` statement add the following *Intent Filter*: 
``` xml
<intent-filter>
    <action android:name="com.mobvoi.semantic.action.FLIGHT"/>
    <category android:name="android.intent.category.DEFAULT"/>
</intent-filter>
```
The tag, `com.mobvoi.semantic.action.FLIGHT`, is a flight search corresponding action (for the list of all actions, see the Appendix later in this article).  Adding this line Intent Filter is equivalent to telling the Ticwear system: "**I am an App for searching flights, when the user uses voice search to check a flight, please let me handle it**." 

2) Analyze Intent in the Activity

The next time the user uses voice to check flights, the system will pass the semantic analysis of Intent results to your designated Activity (If there are multiple third-party applications that can complete a stated action, a selection box will appear) .  Please include our SDK in the project, which contains `SemanticIntentApi`, this class can help you resolve the incoming Intent and extract the appropriate semantic tags. 

Different voice query tasks have different pre-defined semantic tags.  For tasks such as flight search, third-party developers might be most concerned about the flight departure (*from*), destination (*to*) and the date and time of departure (*departure_date*).  In the appendix, we can learn to and from semantic tags are entity type tags; and *departure_date* is a *datetime* type of semantic tag (see appendix for the different types of semantic tags).

We can use the following code to extract corresponding type of semantic tags from the incoming Intent.  For the entity type of semantic tags, use `SemanticIntentApi.extractAsEntity()` function, and for the *datetime* type of semantic tags, use `SemanticIntentApi.extractAsDatetime()` function as follows: 

``` Java
EntityTagValue from = SemanticIntentApi.extractAsEntity(getIntent(), "from");
EntityTagValue to = SemanticIntentApi.extractAsEntity(getIntent(), "to");
DatetimeTagValue time = SemanticIntentApi.extractAsDatetime(getIntent(),
        "departure_date");
```

Please note that before using these semantic tags, please determine whether or not it is null.  Because the user may not mention a semantic query tag information (such as "flights Shanghai to Beijing" omitting the departure time), please handle these situations in the program logic. 

If you have confirmed that semantic tags are not null, you can directly call: 

``` Java
Log.d(TAG, "Get from: " + from.normData);
Log.d(TAG, "Get to: " + to.normData);
Log.d(TAG, "Get departure_date: " + time.rawData);
```

### Getting results in the Semantic Intent

In addition to semantic search results, we also put search results in the Semantic Intent, you can have direct access to our back and semantic query that matches the search results (such as flights, visit lists, etc.)

1) In the Project Build Path import Alibaba fastjson libraries: 

``` Java
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
```

2) Using fastjson library deserialization tool, deserialize incoming Semantic Intent results: 

``` Java
Object searchParams = getIntent().getExtras().get("params");
JSONObject searchRsltJson = (JSONObject) JSON.toJSON(searchParams);
```

3) When searchRsltJson is not null, directly use the results. You can directly searchRsltJson as follows (the following is a flight search result, other types return similar results), please obtain the required fields on your own

``` json
{
      "background": "flight",
      "details": [
        {
          "airline": "上海航空",
          "discount": "6.0折",
          "endTime": "23:45",
          "flightName": "上海航空FM9107",
          "flightNo": "FM9107",
          "from": "上海虹桥机场",
          "linkUrl": "http://u.ctrip.com/union/CtripRedirect.aspx?...",
          "price": "￥740",
          "startTime": "21:30",
          "to": "北京首都机场"
        },
        {
          (omitted)
        }
      ]
    }
```
