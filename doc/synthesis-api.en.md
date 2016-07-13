## Speech Synthesis API

For details, refer to Github inside Ticwear SDK open source project [sample speech synthesis program][synthesis-example].

### Brief introduction

Using Speech Synthesizer API, in Ticwear you can conveniently call on Mobvoi speech synthesis engine, and for a given piece of text, we will provide you with artificial synthesized voice.

### Usage

You only need to achieve our defined `SpeechSynthesizerCallbackInterface` interfaces, as shown in the code:

``` Java
private SpeechSynthesizerCallbackInterface mTTSCallback = new SpeechSynthesizerCallbackInterface() {
    @Override
    public void onStart() {
        Dbg.i(TAG, "Start play synthesized data");
    }

    @Override
    public void onCompletion() {
        Dbg.i(TAG, "Done playing synthesized data");
    }

    @Override
    public void onError(ErrorCode errorCode, String errorMessage) {
        Dbg.d(TAG, "ErrorCode: " + errorCode + " Error Message: " + errorMessage);
    }
}
```

If you do not want to implement your own, you can also use the default `Callback` class `DefaultSpeechSynthesizerCallback`.

``` Java
SpeechSynthesizerCallbackInterface mTTSCallback = new DefaultSpeechSynthesizerCallback();
```

Then call the code below to

``` Java
SpeechSynthesizerApi.startSynthesizer(getApplicationContext(), mTTSCallback, text, 50);
```

When the speech synthesis start playing `onStart()` function will be called back
When the speech synthesis has finished playing time `onCompletion()` function will be called back.
When the speech synthesis process when any error, `onError()` function is called.

[synthesis-example]: https://github.com/ticwear/sdk/tree/master/sample/android-studio/TTSDemo
