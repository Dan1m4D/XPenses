# XPenses

<p align="center">
  <img src="./images-readme/XPENSES_ LOGO.png" width="200px" />
  <h1 align="center">Expenses tracking app - Android project for ICM classes</h1>
  <img src="./images-readme/screens.png"  />
</p>
<hr/>

## About the project

The XPenses application aims to address the problem of keeping track of expenses in a simple and organized way. With this in mind, busy people can easily open the app after a shopping session and simply scan the invoice. In this way, they can not only keep track of their invoices but also plan ahead with valuable information regarding past invoices, such as expenses per category or per time period.

## Structure
```
.
├── outputs.zip
├── README.md
└── XPenses
    ├── app
    ├── build.gradle.kts
    ├── database.rules.json
    ├── gradle
    ├── gradle.properties
    ├── gradlew
    ├── gradlew.bat
    ├── local.defaults.properties
    ├── local.properties
    ├── secrets.properties
    └── settings.gradle.kts
```
The `XPenses` directory holds the **Android Project** itself.
The `outputs.zip` is a zipped apk ready to run.

## Architecture

![alt text](<images-readme/diagram .png>)


## Enable google auth with firebase

### **Get SHA1 key from Android Studio**

Follow [this](https://medium.com/@mr.appbuilder/how-to-get-sha1-key-in-android-studio-current-version-or-newversion-cb90814c14cd) tutorial to obtain your SHA1 key

### **Add your key to the Firebase project**

Access [FireBase Configurations](https://console.firebase.google.com/u/0/project/xpenses-de115/settings/general/android:com.d479.xpenses?hl=pt)
![alt text](images-readme/image.png)

Scroll down until you find
![alt text](images-readme/iimage-1.png)

Click on "Adicionar impressão digital" and insert your SHA1 key.
![alt text](<images-readme/image (1).png>)

Then save

## Configure Maps API

If the project doesn't load the map correctly generate a new key following [this tutorial](https://developers.google.com/maps/documentation/android-sdk/get-api-key) and replace it into `secrets.properties` on `MAPS_API_KEY= ... `
