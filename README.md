# AlphaSms

AlphaSms is a lightweight Android application that automatically forwards received SMS messages to a Telegram chat.

[![Download AlphaSms](https://img.shields.io/badge/Download-AlphaSms%20APK-blue?style=for-the-badge)](../../releases/latest)

The application receives SMS messages in the background, stores them locally, and sends them to Telegram when an Internet connection is available.

## Features

* Automatically receives incoming SMS messages.
* Forwards SMS messages to a Telegram chat.
* Stores SMS messages locally until they are successfully sent.
* Automatically retries delivery if the Internet connection is unavailable.
* Shows the sender, received date and time, and message text in Telegram.
* Telegram Bot Token and Chat ID are stored encrypted using Android Keystore.
* No external server is required - AlphaSms communicates directly with the Telegram Bot API.
* Works with Android 7.0 (API 24) and newer.

## Requirements

* Android 7.0 (API 24) or newer.
* A Telegram account.
* A Telegram bot.
* An Internet connection for sending messages to Telegram.

## Installation

Download the latest APK from the [Releases](../../releases) page and install it on your Android device.

AlphaSms is distributed as an APK and is not currently available through Google Play.

## Telegram Bot Setup

Before using AlphaSms, you need to create a Telegram bot and obtain its Bot Token and Chat ID.

### 1. Create a Telegram bot

In Telegram, open **@BotFather** and create a new bot using the `/newbot` command.

BotFather will provide a **Bot Token**. Keep this token private.

### 2. Get your Chat ID

You can get your Chat ID directly using the Telegram Bot API.

1. Find your bot in Telegram.

2. Open the bot and press **Start**.

3. Send at least one test message to the bot in the chat where you want to receive SMS messages.

4. Open the following URL in a web browser:

   `https://api.telegram.org/bot<TOKEN>/getUpdates`

5. Replace `<TOKEN>` with your Bot Token.

   For example:

   `https://api.telegram.org/bot123456789:ABCdefGHI.../getUpdates`

6. The browser will display a JSON response. Find the following part:

   `"chat":{"id":<CHAT_ID>`

7. Copy the value of `<CHAT_ID>` and enter it into the **Chat ID** field in AlphaSms.

For example, if the response contains:

```text
"chat":{"id":123456789,"first_name":"John",...
```

then your Chat ID is:

```text
123456789
```

**Important:** Keep your Bot Token private. Do not publish it or share it with anyone.

## Initial Configuration

Open AlphaSms and enter:

* **Bot Token** - the token provided by BotFather.
* **Chat ID** - the Telegram chat where SMS messages should be delivered.

Tap **Save**.

You can use the **Test** button to verify that the Telegram configuration works.

The Bot Token and Chat ID are stored locally in encrypted form using Android Keystore.

## SMS Permission

Android will ask AlphaSms for permission to receive SMS messages.

Allow this permission if you want AlphaSms to forward incoming SMS messages.

SMS messages remain available in your regular SMS application as well.

## Background Operation

For reliable background operation, Android may require AlphaSms to be excluded from battery optimization.

If SMS delivery is delayed, check the battery settings for AlphaSms and allow **Unrestricted** background usage, if this option is available on your device.

The exact location and wording of this setting may differ between Android versions and manufacturers.

## Offline Operation

AlphaSms does not require an Internet connection at the moment an SMS is received.

If there is no Internet connection:

1. The SMS is stored locally.
2. AlphaSms waits for a network connection.
3. The message is sent to Telegram when network connectivity becomes available.
4. The message remains stored until successful delivery.

This helps prevent SMS messages from being lost because of a temporary lack of Internet connectivity.

## Privacy and Security

AlphaSms does not use a third-party server to process SMS messages.

The application communicates directly with the Telegram Bot API.

The following configuration data is stored locally on the device:

* Telegram Bot Token
* Telegram Chat ID

These values are encrypted using the Android Keystore.

**Important:** Never share your Bot Token with anyone. Anyone who has access to the token may be able to control your Telegram bot.

## Source Code

The complete source code is available in this repository.

The project is built with:

* Kotlin
* Jetpack Compose
* Android Room
* Android WorkManager
* Android Keystore
* Telegram Bot API

## License

This project currently has no open-source license.

All rights are reserved by the author.
