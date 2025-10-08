#!/bin/bash

# ===============================================================
# Shell script to authenticate with Keycloak and call a secured API
# ===============================================================
#
# Before running, you must have 'curl' and 'jq' installed.
# 'jq' is used to parse the JSON response from Keycloak.
#
# This script uses the password grant type. Make sure "Direct Access Grants"
# is enabled for your client in the Keycloak Admin Console.
#
# NOTE: Replace all placeholder values below with your actual data.
#

# --- API and Keycloak Configuration ---
API_URL="http://localhost:8080"
KEYCLOAK_URL="http://localhost:9080"
REALM="concession"
CLIENT_ID="app"
USERNAME="hwconnors@gmail.com"
PASSWORD="Sh!re7942"
API_ENDPOINT="/api/external/user/me"

# --- Step 1: Request a Bearer Token from Keycloak ---
echo "1. Requesting a bearer token from Keycloak..."

TOKEN_RESPONSE=$(curl -s -X POST "${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "grant_type=password" \
-d "client_id=${CLIENT_ID}" \
-d "username=${USERNAME}" \
-d "password=${PASSWORD}")

# --- Step 2: Extract the access token using 'jq' ---
ACCESS_TOKEN=$(echo "${TOKEN_RESPONSE}" | jq -r '.access_token')

if [[ -z "${ACCESS_TOKEN}" || "${ACCESS_TOKEN}" == "null" ]]; then
    echo "Error: Failed to retrieve access token."
    echo "Response from Keycloak: ${TOKEN_RESPONSE}"
    exit 1
fi

echo "Successfully retrieved token."

# --- Step 3: Call the secured API endpoint with the Bearer Token ---
echo "2. Calling the secured API endpoint: ${API_ENDPOINT}"
echo "   with Authorization: Bearer ${ACCESS_TOKEN:0:10}..." # Display a partial token for security

API_RESPONSE=$(curl -s -X GET "${API_URL}${API_ENDPOINT}" \
-H "Authorization: Bearer ${ACCESS_TOKEN}")

# --- Step 4: Display the API response ---
echo "3. API Response:"
echo "${API_RESPONSE}"
