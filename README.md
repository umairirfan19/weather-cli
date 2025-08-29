[![Java CI](https://github.com/umairirfan19/weather-cli/actions/workflows/run.yml/badge.svg)](https://github.com/umairirfan19/weather-cli/actions/workflows/run.yml)

A minimal, dependency-free Weather CLI that lets you check the current weather for **any city**:

```
java -cp out com.umair.weathercli.App "Toronto"
java -cp out com.umair.weathercli.App "New York"
```

It uses the free **Open‑Meteo** APIs (no API key required):
- Geocoding: `https://geocoding-api.open-meteo.com/v1/search?name={city}&count=1`
- Forecast:  `https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&current_weather=true`

## Run locally

Mac/Linux:
```bash
rm -rf out && mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out com.umair.weathercli.App "Toronto"
```

Windows (Powershell):
```powershell
Remove-Item -Recurse -Force out -ErrorAction Ignore; New-Item -ItemType Directory -Path out | Out-Null
Get-ChildItem -Recurse -Filter *.java src | % FullName | javac -d out @-
java -cp out com.umair.weathercli.App "Toronto"
```

## Project structure

```
src/
  com/umair/weathercli/App.java
  com/umair/weathercli/HttpUtil.java
  com/umair/weathercli/JsonMini.java
.github/workflows/run.yml
README.md
```

## GitHub quick start

1. Create a new empty repo on GitHub (e.g., `weather-cli`).
2. In your terminal inside the project folder:
   ```bash
   git init
   git add .
   git commit -m "Initial commit: Weather CLI"
   git branch -M main
   git remote add origin https://github.com/<your-username>/weather-cli.git
   git push -u origin main
   ```
3. Try the action by running locally or adding more workflow steps.

## Sample command

```
java -cp out com.umair.weathercli.App "Karachi"
```

## Notes

- This is a very small project to keep things simple. For production, consider adding a JSON library like Gson or Jackson for robust parsing.
- Open‑Meteo rate limits are generous for personal use but avoid spamming requests.
