# Permission Helper

[![N|Solid](https://img.shields.io/badge/Android%20Arsenal-PermissionHelper-brightgreen.svg)](https://android-arsenal.com/details/1/5532)

PermissionHelper used to simplfy Runtime Permission Structure.
  - Easy to use.
  - Works with Activity and Fragment directly.
  - Works on Pre-Marshmallow (No need to check for build version condition).
  - Get Callback on exactly same place where you asked for permission.
  - Get Grant callback whenever all the permission you mentioned are granted else Deny callback
  - Get callback whenever some permissions from you mentioned are granted.(New)
  - Get callback when permission is auto denied by system(When user selects, Don't ask again).
  
### Latest Version [2.3]
- As jCenter is deprecated we have switched to jitpack as of now.

### Download
In root level build.gradle
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
Include the following dependency in your apps level build.gradle file.
```
implementation 'com.github.pankaj89:PermissionHelper:2.3'
```

### How to use

#### In Kotlin
```kotlin
permissionHelper = PermissionHelper(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE), 100)
permissionHelper?.denied {
    if (it) {
        Log.d(TAG, "Permission denied by system")
        permissionHelper?.openAppDetailsActivity()
    } else {
        Log.d(TAG, "Permission denied")
    }
}

//Request all permission
permissionHelper?.requestAll {
    Log.d(TAG, "All permission granted")
}

//Request individual permission
permissionHelper?.requestIndividual {
    Log.d(TAG, "Individual Permission Granted")
}
```

#### In Java
```java
permissionHelper = new PermissionHelper(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
permissionHelper.request(new PermissionHelper.PermissionCallback() {
    @Override
    public void onPermissionGranted() {
        Log.d(TAG, "onPermissionGranted() called");
    }
    
    @Override
    public void onIndividualPermissionGranted(String[] grantedPermission) {
        Log.d(TAG, "onIndividualPermissionGranted() called with: grantedPermission = [" + TextUtils.join(",",grantedPermission) + "]");
    }
    
    @Override
    public void onPermissionDenied() {
        Log.d(TAG, "onPermissionDenied() called");
    }

    @Override
    public void onPermissionDeniedBySystem() {
        Log.d(TAG, "onPermissionDeniedBySystem() called");
    }
});
```

### Override onRequestPermissionsResult
```java
@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (permissionHelper != null) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
```

### My Other Libraries
###### Simple Adapter for RecyclerView [(<u><i>link</i></u>)](https://github.com/pankaj89/PermissionHelper)
###### LocationHelper [(<u><i>link</i></u>)](https://github.com/pankaj89/LocationHelper)
###### MasterExoPlayer (Play video inside RecyclerView) [(<u><i>link</i></u>)](https://github.com/pankaj89/MasterExoPlayer)

### License
```
Copyright 2017 Pankaj Sharma

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
