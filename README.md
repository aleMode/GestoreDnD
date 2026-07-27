# GestoreDnD

A native Android app for managing **Dungeons & Dragons 5e characters and campaigns** — build character sheets on your phone, group them into campaigns, and share them with your table through a join link. Sheets live locally so they work with no connection, and sync to the cloud on demand.

Built end-to-end (design, data model, UI, backend integration) in **Kotlin** on **Firebase**.

<!--
Screenshots — replace these placeholders with real captures:
<p align="center">
  <img src="docs/screenshots/login.png"    width="220" alt="Login" />
  <img src="docs/screenshots/sheet.png"    width="220" alt="Character sheet" />
  <img src="docs/screenshots/campaign.png" width="220" alt="Campaign roster" />
</p>
-->

---

## Features

### Characters
- **Full 5e character sheet** split into five tabs — Stats, Abilities, Equipment, Spells, Feats — swapped inside a single activity through a `SheetSwapper` interface implemented by the host activity.
- Core stats (HP, AC, speed, proficiency and initiative bonus), the six ability scores, and ten skill modifiers, each persisted on focus loss so nothing is lost when navigating away.
- **Equipment and backpack** as two independent, editable lists; **spells** tracked with name and level; **feats** with free-text descriptions.
- **Character portrait** from the device camera or gallery, downscaled to 400×400, written to app-private storage and mirrored to Firebase Storage.

### Campaigns
- Create a campaign and become its **DM**; every campaign gets a UUID and a Firestore document.
- **Join by link** — the DM copies a share link to the clipboard, and an App Links intent filter routes it into `JoinCampaignActivity`, where the invitee picks which of their characters to bring.
- Joining pushes a **copy** of the chosen sheet into the campaign, so campaign progress is tracked separately from the player's local character.
- **Role-aware UI**: the DM lands on the party roster and can open and edit any member's sheet; players are routed straight to their own character, which saves directly to the cloud.

### Everything else
- **Email/password authentication** via Firebase Auth, with client-side regex validation on registration (valid address, 8–20 chars, mixed case + digit) and confirmation fields.
- **Offline mode** — skip login entirely and work against local storage only; cloud actions are hidden rather than left to fail.
- **Explicit sync model**: separate upload (push) and sync (pull) actions per list, so the user controls when the network is touched.
- **Login hygiene**: signing in with a different account wipes the previous account's local JSON, preventing cross-account data bleed.
- **Compendium** screen with a bundled quick reference for common 5e roll mechanics.
- **Material 3 theming** with hand-tuned light and dark palettes.

---

## Tech stack

| Area | Choice |
|---|---|
| Language | Kotlin |
| UI | Android Views, Fragments, RecyclerView, Material 3, ViewBinding |
| Auth | Firebase Authentication (email/password) |
| Cloud data | Cloud Firestore (campaigns, shared sheets) |
| Cloud files | Firebase Storage (character/campaign JSON, portraits) |
| Local persistence | Gson-serialized JSON in app-private `filesDir` |
| Async | Kotlin coroutines over the Play Services task API |
| Build | Gradle, AGP 7.3, `minSdk 21` / `targetSdk 32` |

### Storage design

The app uses two cloud tiers with different jobs:

- **Firebase Storage** — the user's *private* data, kept as the same JSON files the app uses locally. `{uid}/characters.json` is the index, `{uid}/{name}.json` is each full sheet, `{uid}/{name}.jpg` is the portrait. Upload and download are straight file transfers, which makes local and remote state trivially interchangeable.
- **Cloud Firestore** — the *shared* data. `groups/{campaignId}/data/{campaignId}` holds campaign metadata and the member list; `groups/{campaignId}/chars/{uid}.json` holds each player's in-campaign sheet as a structured document, so the DM can read the whole party in a single query.

---

## Project structure

```
app/src/main/java/com/example/gestorednd/
├── Activities/
│   ├── MainActivity.kt            # login/registration host, offline entry point
│   ├── MenuActivity.kt            # characters ⇄ campaigns chip navigation
│   ├── SheetActivity.kt           # character sheet host, implements SheetSwapper
│   ├── CampaignActivity.kt        # DM roster + share link
│   ├── JoinCampaignActivity.kt    # deep-link entry point, join flow
│   └── CompendiumActivity.kt      # 5e rules reference
├── MainMenuFragments/             # character list, campaign list (+ sync/upload)
├── CharacterSheetFragments/       # Stats, Ability, Equipment, Spells, Feats, Nav
├── StartFragments/                # Login, Registration
├── Adapters/                      # RecyclerView adapters (plain and selectable)
├── DataClasses/                   # Pg, Characters, Campaigns, Spells, Feats, ...
└── Interfaces/SheetSwapper.kt     # fragment-swap contract for the sheet tabs
```

`Characters` is the lightweight list-row model (name, species, class, level); `Pg` is the full sheet. The split keeps the character list cheap to load and render without deserializing every sheet.

---

## Getting started

### Requirements
- Android Studio with Android SDK 32
- JDK 11+
- A Firebase project

### Firebase setup

The committed `app/google-services.json` points at the original development project, whose backend is not open for public use — **replace it with your own**:

1. Create a Firebase project and register an Android app with the package name `com.example.gestorednd`.
2. Enable **Authentication → Email/Password**, **Cloud Firestore**, and **Storage**.
3. Download your `google-services.json` into `app/`.
4. Apply security rules that scope each user to their own data — for example, restrict `{uid}/**` in Storage to that uid, and gate `groups/{id}` writes on campaign membership.

### Build and run

```bash
git clone https://github.com/<your-username>/GestoreDnD.git
cd GestoreDnD
./gradlew assembleDebug          # or: ./gradlew installDebug
```

Or open the project in Android Studio and hit Run.

---

## Author

**Alessandro Modena** — designed and written entirely by me.

> *Dungeons & Dragons is a trademark of Wizards of the Coast. This is an unofficial fan project with no affiliation.*
