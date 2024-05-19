import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

export const onPasswordChange = functions.firestore
  .document("users/{userId}/passwords/{passwordId}")
  .onWrite(async (change, context) => {
    const userId = context.params.userId;
    const newValue = change.after.exists ? change.after.data() : null;
    const previousValue = change.before.exists ? change.before.data() : null;

    let notification: { title: string; body: string; channelId: string } | null = null;

    if (!newValue) {
      // Document deleted
      notification = {
        title: "Password Deleted",
        body: `Password with title "${previousValue?.title}" has been deleted.`,
        channelId: "vault_password_deletion_channel",
      };
    } else if (!previousValue) {
      // Document created
      notification = {
        title: "Password Added",
        body: `Password with title "${newValue?.title}" has been added.`,
        channelId: "vault_password_creation_channel",
      };
    } else {
      // Document updated
      notification = {
        title: "Password Updated",
        body: `Password with title "${newValue?.title}" has been updated.`,
        channelId: "vault_password_update_channel",
      };
    }

    if (notification) {
      const userTokensSnapshot = await admin.firestore()
        .collection("users")
        .doc(userId)
        .collection("tokens")
        .get();

      const tokens = userTokensSnapshot.docs.map((doc) => doc.id);

      if (tokens.length > 0) {
        const payload = {
          tokens,
          notification: {
            title: notification.title,
            body: notification.body,
          },
          android: {
            notification: {
              channelId: notification.channelId,
            },
          },
          apns: {
            payload: {
              aps: {
                sound: "default",
                alert: {
                  title: notification.title,
                  body: notification.body,
                },
              },
            },
          },
        };

        try {
          const response = await admin.messaging().sendMulticast(payload);
          console.log(`Notifications sent successfully to ${response.successCount} devices.`);
        } catch (error) {
          console.error("Error sending notification:", error);
        }
      }
    }

    return null;
  });
