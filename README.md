# YouTubeMusic Paper Plugin (prototype)

A Java + Gradle prototype for Paper 26.2. It plays **sound events already present in a server resource pack**. It does not stream audio from YouTube and does not download YouTube media.

## Requirements

- Paper 26.2
- Java 25 JDK for building and Java 25 runtime for Paper 26.2
- Gradle 8.14+ or a compatible Gradle installation
- A resource pack hosted at a public HTTPS URL

Paper's current 26.x API uses coordinates such as `26.2.build.+`; if that version is unavailable in your repository at build time, replace it with the exact stable API build your server uses.

## Build

From this folder, run:

```bash
gradle clean build
```

The output JAR will be under `build/libs/YouTubeMusic-0.1.0.jar`.

This environment did not compile the JAR; use JDK 25 and Gradle to build and test it.

## Install

1. Build the JAR.
2. Copy `build/libs/YouTubeMusic-0.1.0.jar` into the server's `plugins/` folder.
3. Start the Paper server once.
4. Edit `plugins/YouTubeMusic/config.yml`.
5. Set `resource-pack.url` to a public HTTPS URL for your ZIP pack and optionally set its SHA-1 hash.
6. Restart the server.

## Commands

- `/music help`
- `/music pack`
- `/music play <namespace:sound>`
- `/music stop`
- `/music youtube <URL>` (admin-only URL validation; does not download or play the video)

Permissions:

- `youtubemusic.play`
- `youtubemusic.control`
- `youtubemusic.admin`

## Resource pack example

Pack layout:

```text
music-pack.zip
├── pack.mcmeta
└── assets/
    └── servermusic/
        ├── sounds.json
        └── sounds/
            └── sample.ogg
```

Example `assets/servermusic/sounds.json`:

```json
{
  "sample": {
    "sounds": [
      {
        "name": "servermusic:sample",
        "stream": true
      }
    ]
  }
}
```

Then `/music play servermusic:sample` plays that sound event for the command sender. The actual OGG file should be placed at `assets/servermusic/sounds/sample.ogg`.

`pack.mcmeta` format must match the target Minecraft version. Use the pack format documented for the exact client build.

## YouTube link limitation

The `/music youtube` command currently validates the URL only. YouTube URLs are not direct OGG audio files. To support import, implement a separate rights-aware audio-ingest service that accepts only media the operator owns or is authorized to use, converts it to OGG Vorbis, updates `sounds.json`, rebuilds the pack, hosts it, and updates the pack URL/hash. A resource pack update may require clients to download/apply the new pack.

## Prototype limitations

- This is a prototype, not a production music player.
- Playback is triggered for the command sender at their location; sound event availability depends on the installed pack.
- `/music stop` stops the MASTER category, which may stop other sounds too. Production code should track a dedicated sound channel/event strategy.
- Dynamic resource-pack regeneration, queue management, duration tracking, and automatic YouTube ingestion are not implemented.
- Do not use this plugin to redistribute copyrighted music without permission.


## Build on your phone with GitHub Actions

1. Extract this ZIP on your phone.
2. Create a GitHub repository and upload the **contents** of this folder, including the hidden `.github` directory. Easiest: use GitHub's web upload and ensure `.github/workflows/build.yml` is included; if uploading a ZIP via the browser isn't supported for repository contents, extract it first.
3. Open the repository's **Actions** tab.
4. Select **Build YouTubeMusic Plugin**, then **Run workflow** (or push to `main`/`master`).
5. When the run succeeds, open the run summary and download the `YouTubeMusic-paper-plugin` artifact ZIP. Extract it to get the plugin JAR.

Note: the workflow automates compilation, but does not guarantee that the currently configured Paper API coordinate exists. If dependency resolution fails, update the Paper API dependency in `build.gradle.kts` to a published build compatible with your actual server version. The plugin is a prototype; YouTube URL handling only validates links and does not download media.
