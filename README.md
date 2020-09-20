# Loan Management System

Backend Service and Angular frontend for a loan management app.


#Backend API documentation
To get the api documentation make sure the application is running
Then enter the url http://localhost:8088/swagger-ui.html or 
http://yourdomain.com:8088/swagger-ui.html where yourdomain.com is the 
the name of your domain as applicable in your case on your browser.
An API documentation of all the backend services is provided in a user 
friendly manner. Explore it.

You can also get a feel of how of the pay load of each service end-point
by exploring the model section of the API


Parameters that needs to be define as system variables before the 
system can work fine include:

PATH_UPLOAD: This is the path where uploaded files are stored on the server e.g /usr/customer/
PATH_UPLOAD_REPORT: This is the path where processed files reports are stored on the server e.g /usr/report/
DB_DATABASE: The name of the database the application is connecting to
DB_USERNAME: The username of the database the system is connecting to
DB_PASSWORD: The password of the database the system is connecting to

These parameters are specified in the application configuration file otherwise known as
application.properties file


