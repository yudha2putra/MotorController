# Tandebike | Secure Keyless Motorcycle

Tandebike Apps is application that works to rent a motorcycle in public place. Motorcycle will be installed with embedded system that can turn on / turn off the machine using this application.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

Dependencies that you need to install is

```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    implementation 'com.android.volley:volley:1.1.1'
}
```
This application have 8 Java Classes and 6 Layout


## Splashscreen Screen

This screen will appear first when the application is opened

### OnCreate

When it called, application will display activity_splash_screen, after 3 Second it will switch to LoginActivity Class

```
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        }, 3000L); //3000 L = 3 detik
    }
```
