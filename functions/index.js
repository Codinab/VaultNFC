const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.onPasswordChange = functions.firestore
    .document("users/{userId}/passwords/{passwordId}")
    .onWrite(async (change, context) => {
      const userId = context.params.userId;
      const passwordId = context.params.passwordId;
      const newValue = change.after.exists ? change.after.data() : null;
      const previousValue = change.before.exists ? change.before.data() : null;

      let notification = null;

      if (!newValue) {
        // Document deleted
        notification = {
          title: "Password Deleted",
          body: `Password with title "${previousValue.title}" has been deleted.`,
          channelId: "vault_password_deletion_channel",
        };
      } else if (!previousValue) {
        // Document created
        notification = {
          title: "Password Added",
          body: `Password with title "${newValue.title}" has been added.`,
          channelId: "vault_password_creation_channel",
        };
      } else {
        // Document updated
        notification = {
          title: "Password Updated",
          body: `Password with title "${newValue.title}" has been updated.`,
          channelId: "vault_password_update_channel",
        };
      }

      if (notification) {
        const userTokens = await admin.firestore().collection("users").doc(userId).collection("tokens").get();

        const tokens = [];
        userTokens.forEach((doc) => {
          tokens.push(doc.id);
        });

        if (tokens.length > 0) {
          const payload = {
            notification: notification,
          };

          try {
            await admin.messaging().sendMulticast({tokens, ...payload});
            console.log("Notification sent successfully.");
          } catch (error) {
            console.error("Error sending notification:", error);
          }
        }
      }

      return null;
    });
