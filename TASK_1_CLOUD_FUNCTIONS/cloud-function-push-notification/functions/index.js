const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);

exports.pushNotification = functions.database.ref("/movies/{pushId}")
    .onWrite((change, context) => {
      console.log("Push notification event triggered");

      //  Get the current value of what was written to the Realtime Database.
      const valueObject = change.after.val();

      // Create a notification
      const payload = {
        notification: {
          title: valueObject.name,
          body: String(valueObject.rating),
          sound: "default",
        },
      };

      console.log(valueObject.name);
      console.log(String(valueObject.rating));

      const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24,
      };

      return admin.messaging()
          .sendToTopic("pushNotifications", payload, options);
    });

