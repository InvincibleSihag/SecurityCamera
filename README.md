# SecurityCamera
A project based on Camera security system with Storage Optimization and Computer Vision

The api.py file contains the main code.

# Working 

This project contains two side codes.
Python code - Server side
Android app - Client side

Python code will run on Raspberry pi with camera attached. I have implemented Flask API in this project to communicate with client.
This code will start streaming live video from raspberry pi to the android app over local network.
Computer Vision methods applied in this project will track motion in frames and will only record the frames which contains motion, hence this method will save a lot of storage.
A pre trained face detection model is also being added to detect faces and save them in particular directory with Date and Time mentioned.

# Android app

For security of the application, google firebase login is added. 
The app will show the live stream video of camera (required phone is connected to same network)
In this app , we can check the saved videos and faces on server.
Flask api took care of requests sent from the app to server. Based on the request, desired data is sent to the app from server (raspberrypi)
