# GitHub User Activity CLI (Java)

A beginner-friendly command-line app that fetches a GitHub user's recent public activity and prints readable messages.

## What This Project Does

Given a GitHub username, the app:
1. Calls the GitHub Events API: `https://api.github.com/users/<username>/events`
2. Reads the JSON response
3. Converts events into readable lines
4. Prints the latest 10 events

How Java does API calls (simple idea)

Java uses something like:

👉 HttpURLConnection

Flow:

1. Create URL
2. Open connection
3. Send request
4. Read response


