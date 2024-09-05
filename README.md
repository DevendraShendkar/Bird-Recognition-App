# Bird Species Recognition App

This app uses machine learning to recognize bird species from images. It is built using Kotlin for Android development and utilizes a model created in Python. The app includes a feature to search for bird species, which integrates with Firebase for storing and retrieving bird images.

### ScreenShots
<table>
  <tr>
    <td style="padding: 10px;">
      <img src="https://github.com/user-attachments/assets/53f511db-3bf2-4f7d-a4ca-7dcb5f07aa8d" width="350" />
    </td>
    <td style="padding: 10px;">
      <img src="https://github.com/user-attachments/assets/1a6e9d32-7be7-430f-b0b8-21cbd8cdb514" width="350" />
    </td>
    <td style="padding: 10px;">
      <img src="https://github.com/user-attachments/assets/05d02a92-22ab-45de-a71c-0cd4929e7a15" width="350" />
    </td>
  </tr>
</table>


## Features

- **Bird Species Recognition**: Uses a pre-trained machine learning model to identify bird species from images.
- **Firebase Integration**: Allows uploading and searching for bird images.

## Setup

### Prerequisites

- Android Studio
- Firebase account
- Python environment (for model creation)

### Firebase Setup

1. **Create a Firebase Project**:
   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Click on "Add project" and follow the prompts to create a new Firebase project.

2. **Add Firebase to Your Android App**:
   - In the Firebase Console, select your project.
   - Click on the Android icon to add Firebase to your Android app.
   - Register your app with the package name (e.g., `com.example.birdrecognition`).
   - Download the `google-services.json` file and place it in your app’s `app/` directory.

3. **Upload Bird Images**:
   - In the Firebase Console, navigate to "Storage" and create a storage bucket.
   - Upload bird images to the bucket to be used by the app.

### Running the App

1. **Clone the Repository**:
   ```sh
   git clone https://github.com/DevendraShendkar/Bird-Recognition-App.git
   cd bird-species-recognition-app


   ## License

This project is licensed under the Apache License 2.0. See the [LICENSE.txt](LICENSE.txt) file for details.

