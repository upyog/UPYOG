import os
import subprocess
import openai
import requests
import json

# GitHub environment vars
REPO = os.getenv("GITHUB_REPOSITORY")
PR_NUMBER = os.getenv("GITHUB_REF").split('/')[-1] if 'refs/pull/' in os.getenv("GITHUB_REF", '') else None
GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")

# Get PR diff
diff = subprocess.check_output(
    ['git', 'diff', 'origin/main...HEAD'],
    text=True
)

# Compose prompt
prompt = (
    "You are a senior software engineer and expert code reviewer. "
    "When provided with code diffs, perform a detailed structured review. "
    "Break feedback into sections:\n"
    "1. Summary of Code Changes\n"
    "2. Code Quality Issues\n"
    "3. Suggestions for Improvement\n"
    "4. Overall Assessment\n"
    "Be constructive, concise, and professional."
)

openai.api_key = OPENAI_API_KEY

# Call OpenAI API
response = openai.ChatCompletion.create(
    model="gpt-3.5-turbo",
    messages=[
        {"role": "system", "content": prompt},
        {"role": "user", "content": diff}
    ]
)

ai_feedback = response['choices'][0]['message']['content']

# Post comment to the PR
if PR_NUMBER and REPO and ai_feedback:
    api_url = f"https://api.github.com/repos/{REPO}/issues/{PR_NUMBER}/comments"
    headers = {
        "Authorization": f"Bearer {GITHUB_TOKEN}",
        "Accept": "application/vnd.github.v3+json"
    }
    r = requests.post(api_url, headers=headers, data=json.dumps({"body": f"### AI Review\n{ai_feedback}"}))
    if r.status_code >= 400:
        print(f"Failed to post comment: {r.text}")
else:
    print("Not running on a pull request or variables missing.")
