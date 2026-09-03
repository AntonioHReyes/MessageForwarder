# MessageForwarder 📱➡️💬

> *"Why carry two phones when you can be smart lazy?"* - Me, probably

## What the hell is this? 🤔

Ever had a phone that's basically your entire digital life - bank accounts, 2FA codes, all your important stuff tied to that number - but you leave it at home because you're paranoid about getting robbed?

Yeah, me too.

The problem? You need those damn verification codes that just got texted to your safe-at-home phone while you're out trying to pay for groceries.

**WELL, NOT ANYMORE.**

This app captures ALL SMS messages that arrive on your "vault phone" (the one chilling safely at home) and forwards them straight to Telegram. So even if your daily-carry phone gets stolen, your digital life stays protected at home while you still get all your codes. Big brain move 🧠

## How do I use this? 🛠️

### Requirements
- Android 9.0+ (if you have less... dude, upgrade)
- A Telegram bot (don't worry, it's free and I'll tell you how)
- The desire to be paranoid-efficient

### Quick Installation

1. **Clone this:**
```bash
git clone https://github.com/AntonioHReyes/MessageForwarder.git
cd MessageForwarder
```

2. **Build and install:**
```bash
./gradlew installDebug
```

3. **Set up your Telegram bot** (keep reading below 👇)

## Setting up the Telegram Bot (Don't panic, it's easy) 🤖

### Step 1: Create Your Bot
1. Open Telegram and search for `@BotFather` (it's official, not a scam)
2. Send `/newbot` and follow the instructions
3. You'll get a really long TOKEN, **SAVE IT!**
   - Looks something like: `123456789:ABCdefGHIjklMNOpqrsTUVwxyz1234567`

### Step 2: Get Your Chat ID
1. Send any message to your new bot (e.g., "Hello there")
2. Open this in your browser (replace `<YOUR_TOKEN>` with your actual token):
   ```
   https://api.telegram.org/bot<YOUR_TOKEN>/getUpdates
   ```
3. Look for `"chat":{"id":` and copy that number
4. That's your **Chat ID** (something like `987654321`)

### Step 3: Configure the App
1. Open the app on your phone
2. Tap the settings icon ⚙️
3. Paste your Bot Token and Chat ID
4. Tap "Test Connection" (you should get a message on Telegram)
5. If it works, hit "Save"
6. Go back to the main screen and tap "Start Service"
7. Accept the permissions (yes, it needs to read your SMS, obviously)

### Step 4: You're Done! 🎉
Now every SMS that arrives will automagically appear in your Telegram chat. That easy.

## Tech Stack (for the nerds) 🤓

- **Kotlin** - Because Java is so 2010
- **Jetpack Compose** - Declarative UI that doesn't give you cancer
- **Ktor Client** - For HTTP requests (learning new things)
- **Kotlinx Serialization** - JSON without the headaches
- **DataStore** - SharedPreferences but without the PTSD
- **Navigation Compose** - So you don't get lost between screens
- **Coroutines** - Async/await but cooler

## Architecture 🏗️

```
app/
├── data/
│   ├── models/          # Data classes
│   ├── local/           # DataStore (preferences)
│   ├── remote/          # Ktor client
│   └── repository/      # Business logic
├── service/
│   ├── SmsReceiver      # Captures SMS
│   ├── SmsForwarderService  # Forwards to Telegram
│   └── BootReceiver     # Restarts after boot
└── ui/
    ├── home/            # Main screen
    ├── settings/        # Configuration
    └── navigation/      # NavGraph
```

## License 📜

MIT License - Do whatever you want with this, just don't sue me if something explodes.

## Credits 👨‍💻

Made with ❤️  by [@AntonioHReyes](https://github.com/AntonioHReyes)

*Inspired by the paranoia of getting robbed and the need to keep your digital life accessible*

---

## Buy me a coffee

[![Buy Me A Coffee](https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png)](https://buymeacoffee.com/anhr9728w)

---

**Disclaimer:** I'm not responsible if your significant other reads your verification SMS and discovers how many Amazon accounts you have. Use at your own risk 😅 ‍♂️
